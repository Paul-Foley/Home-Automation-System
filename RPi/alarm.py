#!/usr/bin/env python
#
# homeSec alarm v1.12
#

# import libraries
import RPi.GPIO as GPIO  # GPIO Library
import time, math
import subprocess
import os

# set to use board pin numbering
GPIO.setmode(GPIO.BOARD)
GPIO.setwarnings(False)

# RGB led pins
GPIO.setup(16, GPIO.OUT)
GPIO.setup(18, GPIO.OUT)
# GPIO.setup(22,GPIO.OUT)

# alarm/buzzer set pin
GPIO.setup(15, GPIO.OUT)


# alarm buzzer
def buzz(pitch, duration):
    newpid = os.fork()  # create new process to run parallel
    if newpid == 0:  # if 0 this is the child process
        flash()  # use the child process to call the light flash function
    else:
        period = 1.0 / pitch  # period of cycle
        delay = period / 2  # delay half of period (2 delays per cycle)
        cycles = int(duration * pitch)  # total number of cycles needed for duration specified
    for i in range(cycles):  # turn buzzer on and off for number of cycles needed
        GPIO.output(15, True)
        time.sleep(delay)
        GPIO.output(15, False)
        time.sleep(delay)


# red/blue flash led
def flash():
    while True:
        GPIO.output(16, True)  # True means that LED turns on
        time.sleep(2)
        GPIO.output(16, False)  # False means that LED turns off
        time.sleep(.5)
        GPIO.output(18, True)  # True means that LED turns on
        time.sleep(2)
        GPIO.output(18, False)
        time.sleep(.5)  # delay n seconds
        GPIO.output(16, True)  # True means that LED turns on
        time.sleep(2)
        GPIO.output(16, False)  # False means that LED turns off
        time.sleep(.5)
        GPIO.output(18, True)  # True means that LED turns on
        time.sleep(2)
        GPIO.output(18, False)

# run alarm and lights
buzz(500, 60)

print("Cleaning up")
GPIO.cleanup()
