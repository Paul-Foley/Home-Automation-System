#!/bin/bash
#script to start all programs 1-6 

echo "kickstarter!!!"

#start programs
echo "1. Starting frontrooom.py..."
sudo python frontroom.py&
echo "2. Starting bathrooom.py..."
sudo python bathroom.py&
echo "3. Starting kitchen.py..."
sudo python kitchen.py&
echo "4. Starting camera.py..."
sudo python camera.py&
echo "5. Starting sensor.sh..."
sudo ./sensor.sh&
echo "6. Starting alarm_reset.py..."
sudo python alarm_reset.py&

echo " ***** Started All Programs Sucessfully ***** "

exit 0