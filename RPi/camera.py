#!/usr/bin/env python
#
# homeSec camera v1.12
#

# import libraries
import sys
import ssl
import json
import paho.mqtt.client as mqtt
import picamera
import time
from time import sleep
import subprocess


# establish connection with the aws/server
def on_connect(mqtt_cm, userdata, flags, rc):
    print ("Subscriber Connection status code: " + str(rc) + " | Connection status: successful")
    mqtt_cm.subscribe("$aws/things/camera/shadow/update/accepted", qos=0)


# called when a message is received by a topic
def on_message(mqtt_cm, userdata, msg):
    while True:
        try:
            message_json = json.loads(str(msg.payload))
            print(message_json)
            if message_json['state']['reported']['status'] == 1:
                print(">>> Taking picture Now <<<")

                # set up camera
                camera = picamera.PiCamera()

                # adjust camera position vertical/horizontal
                camera.vflip = True
                camera.hflip = True

                # get current date & time
                now = time.strftime("%c")

                # take still image & use date and time as name of image
                camera.capture(now + '.jpg')

                print("*** image captured ***")

                break
            elif message_json['state']['reported']['status'] == 0:
                print(" 0 received")
                break
        except:
            print("Error: message not recognised")
            break

        try:
            if message_json['state']['reported']['status'] == 3:
                print(">>> Video Recording Now <<<")
                # set up camera
                camera = picamera.PiCamera()

                # adjust camera position vertical/horizontal
                camera.vflip = True
                camera.hflip = True

                # start video
                camera.start_recording('video.h264')

                # record for 10 seconds
                sleep(10)

                # end
                camera.stop_recording()

                # convert video to mp4
                subprocess.call('sudo MP4Box -fps 30 -add video.h264 vid_cap.mp4', shell=True)

                print("video capture has finished | use omxplayer vid_cap.mp4 to play")

                break
            elif message_json['state']['reported']['status'] == 0:
                print(" 0 received")
                break
        except:
            print("Error: message not recognised")
            break


# creating a client with client-id=mqtt-test
mqtt_cm = mqtt.Client()
mqtt_cm.on_connect = on_connect
mqtt_cm.on_message = on_message

# Configure network encryption and authentication options. Enables SSL/TLS support.
# adding client-side certificates and enabling tlsv1.2 support as required by aws-iot service
mqtt_cm.tls_set(ca_certs="/home/pi/HomeSecDevice/root-CA.crt",
                certfile="/home/pi/HomeSecDevice/92951bb681-certificate.pem.crt",
                keyfile="/home/pi/HomeSecDevice/92951bb681-private.pem.key",
                tls_version=ssl.PROTOCOL_TLSv1_2,
                ciphers=None)

# connect to aws-account-specific-iot-endpoint
mqtt_cm.connect("AH5PU35LC0GJH.iot.eu-west-1.amazonaws.com", port=8883)  # AWS IoT service hostname and portno
try:
    # automatic reconnect
    mqtt_cm.loop_forever()
finally:
    print("Cleaning up")
GPIO.Cleanup()
