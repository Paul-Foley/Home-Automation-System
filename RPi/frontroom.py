#!/usr/bin/env python
#
# homeSec frontroom light2 v1.11
#

# import libraries
import sys
import ssl
import json
import paho.mqtt.client as mqtt
import RPi.GPIO as GPIO  # GPIO Library

GPIO.setmode(GPIO.BOARD)  # set to use board pin numbering
GPIO.setwarnings(False)
GPIO.setup(7, GPIO.OUT)  # set up Pin 7


# establish connection with the aws/server
def on_connect(mqtt_fr, userdata, flags, rc):
    print ("Subscriber Connection status code: " + str(rc) + " | Connection status: successful")
    mqtt_fr.subscribe("$aws/things/Light2/shadow/update/accepted", qos=0)
    # mqtt_fr.subscribe("$aws/things/Light2/shadow/update/delta",qos=0)

# called when a message is received by a topic
def on_message(mqtt_fr, userdata, msg):
    while True:
        try:
            message_json = json.loads(str(msg.payload))
            print(message_json)
            if message_json['state']['reported']['status'] == 1:
                print(">>> LIGHT ON <<<")
                GPIO.output(11, True)  # turn on pin 11
                # payload = message_json
                # mqtt_fr.publish("aws/things/Light2/shadow/update", payload, 0, True)
                break
            elif message_json['state']['reported']['status'] == 0:
                print("--- light off ---")
                GPIO.output(11, False)  # turn off pin 11
                # payload = message_json
                # mqtt_fr.publish("aws/things/Light2/shadow/update", payload, 0, True)
                # GPIO.cleanup
                break
        except:
            print("Error: message not recognised")

        try:
            if message_json['state']['status'] == 1:
                print(">>> LIGHT ON <<<")
                GPIO.output(11, True)  # turn on pin 11
                break
            elif message_json['state']['status'] == 0:
                print("--- light off ---")
                GPIO.output(11, False)  # turn off pin 11
                # GPIO.cleanup
                break
        except:
            print("Error: message not recognised")

# create a client
mqtt_fr = mqtt.Client()
mqtt_fr.on_connect = on_connect
mqtt_fr.on_message = on_message

# Configure network encryption and authentication options. Enables SSL/TLS support.
# adding client-side certificates and enabling tlsv1.2 support as required by aws-iot service
mqtt_fr.tls_set(ca_certs="/home/pi/HomeSecDevice/root-CA.crt",
                certfile="/home/pi/HomeSecDevice/92951bb681-certificate.pem.crt",
                keyfile="/home/pi/HomeSecDevice/92951bb681-private.pem.key",
                tls_version=ssl.PROTOCOL_TLSv1_2,
                ciphers=None)

# connect to aws-account-specific-iot-endpoint
mqtt_fr.connect("AH5PU35LC0GJH.iot.eu-west-1.amazonaws.com", port=8883)  # AWS IoT service hostname and portno

try:
    # automatic reconnect
    mqtt_fr.loop_forever()
finally:
    print("Cleaning up")
GPIO.cleanup()
