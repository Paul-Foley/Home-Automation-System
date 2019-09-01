#!/usr/bin/env python
import sys
import string
import boto3
import picamera

# from time import sleep
camera = picamera.PiCamera()

camera.vflip = True
camera.hflip = True

# read date and time parameters
date = str(sys.argv[1])
time = str(sys.argv[2])

# take picture
camera.capture(date + time + '.jpg')

# upload
s3 = boto3.resource('s3')

data = open(date + time + '.jpg', 'rb')
s3.Bucket('pgroup4-images').put_object(Key='images/' + date + time + '.jpg', Body=data)
