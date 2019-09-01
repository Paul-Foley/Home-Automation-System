#!/usr/bin/env python
#
# homeSec bathroom light3 v1.02
#

# import libraries
import sys
import ssl
import json
import paho.mqtt.client as mqtt
import RPi.GPIO as GPIO  # GPIO Library

GPIO.setmode(GPIO.BOARD)  # set to use board pin numbering
GPIO.setwarnings(False)
GPIO.setup(12, GPIO.OUT)  # set up Pin 12


# establish connection with the aws/server
def on_connect(mqtt_br, userdata,  flags, rc):
    print ("Subscriber Connection status code: " + str(rc) + " | Connection status: successful")
    mqtt_br.subscribe("$aws/things/Light3/shadow/update/accepted", qos=0)


# mqtt_br.subscribe("$aws/things/Light3/shadow/update/delta",qos=0)

# called when a message is received by a topic
def on_message(mqtt_br, userdata, msg):
    while True:
        try:
            message_json = json.loads(str(msg.payload))
            print(message_json)
            if message_json['state']['reported']['status'] == 1:
                print(">>> LIGHT ON <<<")
                GPIO.output(12, True)  # turn on pin 12
                # payload = message_json
                # mqtt_br.publish("aws/things/Light2/shadow/update", payload, 0, True)
                break
            elif message_json['state']['reported']['status'] == 0:
                print("--- light off ---")
                GPIO.output(12, False)  # turn off pin 12
                # payload = message_json
                # mqtt_br.publish("aws/things/Light2/shadow/update", payload, 0, True)
                # GPIO.cleanup
                break
        except:
            print("Error: message not recognised")

        try:
            if message_json['state']['status'] == 1:
                print(">>> LIGHT ON <<<")
                GPIO.output(12, True)  # turn on pin 12
                break
            elif message_json['state']['status'] == 0:
                print("--- light off ---")
                GPIO.output(12, False)  # turn off pin 12
                # GPIO.cleanup
                break
        except:
            print("Error: message not recognised")

            # creating a client with client-id=mqtt-test


mqtt_br = mqtt.Client()
mqtt_br.on_connect = on_connect
mqtt_br.on_message = on_message

# Configure network encryption and authentication options. Enables SSL/TLS support.
# adding client-side certificates and enabling tlsv1.2 support as required by aws-iot service
mqtt_br.tls_set(ca_certs="/home/pi/HomeSecDevice/root-CA.crt",
                certfile="/home/pi/HomeSecDevice/92951bb681-certificate.pem.crt",
                keyfile="/home/pi/HomeSecDevice/92951bb681-private.pem.key",
                tls_version=ssl.PROTOCOL_TLSv1_2,
                ciphers=None)

# connect to aws-account-specific-iot-endpoint
mqtt_br.connect("AH5PU35LC0GJH.iot.eu-west-1.amazonaws.com", port=8883)  # AWS IoT service hostname and portno

try:
    # automatic reconnect
    mqtt_br.loop_forever()
finally:
    print("Cleaning up")
GPIO.cleanup()
