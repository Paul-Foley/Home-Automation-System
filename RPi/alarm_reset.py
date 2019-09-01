#!/usr/bin/env python
#
# homeSec alarm_reset v1.05
#

# import libraries
import sys
import ssl
import json
import paho.mqtt.client as mqtt
import RPi.GPIO as GPIO  # GPIO Library
import time, math
import subprocess

# set to use board pin numbering
GPIO.setmode(GPIO.BOARD)
GPIO.setwarnings(False)
GPIO.setup(22, GPIO.OUT)  # set up Pin 22


# establish connection with the server
def on_connect(mqtt_alrm, userdata, flags, rc):
    print ("Subscriber Connection status code: " + str(rc) + " | Connection status: successful")
    mqtt_alrm.subscribe("$aws/things/Alarm/shadow/update/accepted", qos=0)
    # mqtt_alrm.subscribe("$aws/things/Alarm/shadow/update/delta",qos=0)


# called when a message is received by a topic
def on_message(mqtt_alrm, userdata, msg):
    while True:
        try:
            message_json = json.loads(str(msg.payload))
            print(message_json)
            if message_json['state']['reported']['status'] == 0:
                print(">>> RESET ALARM <<<")
                # GPIO.output(15,False) #turn off pin for buzzer
                # GPIO.output(16,False)    # turns off red led
                # GPIO.output(18,False)    # turn off blue led
                GPIO.cleanup()
                subprocess.call('sudo ./alarm_kill.sh', shell=True)
            break
        except:
            print("Error:message not recognised")

        try:
            message_json = json.loads(str(msg.payload))
            if message_json['state']['reported']['status'] == 1:
                print(">>> ALARM SET<<<")
                subprocess.call('sudo ./sec_picam.sh')
                GPIO.output(22, True)  # turn on green led
            break
        except:
            print("Error:message not recognised")


# create client
mqtt_alrm = mqtt.Client()
mqtt_alrm.on_connect = on_connect
mqtt_alrm.on_message = on_message

# Configure network encryption and authentication options. Enables SSL/TLS support.
# adding client-side certificates and enabling tlsv1.2 support as required by aws-iot service
mqtt_alrm.tls_set(ca_certs="/home/pi/HomeSecDevice/root-CA.crt",
                  certfile="/home/pi/HomeSecDevice/92951bb681-certificate.pem.crt",
                  keyfile="/home/pi/HomeSecDevice/92951bb681-private.pem.key",
                  tls_version=ssl.PROTOCOL_TLSv1_2,
                  ciphers=None)

# connect to aws-account-specific-iot-endpoint
mqtt_alrm.connect("AH5PU35LC0GJH.iot.eu-west-1.amazonaws.com", port=8883)  # AWS IoT service hostname and portno

try:
    # automatic reconnect
    mqtt_alrm.loop_forever()
finally:
    print("Cleaning up")
GPIO.Cleanup()
