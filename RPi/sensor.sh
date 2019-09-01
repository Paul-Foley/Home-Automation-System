#!/bin/bash
#this script sets the alarm/infra sensor

echo "Alarm Set"
echo "4" > /sys/class/gpio/export
echo "in" > /sys/class/gpio/gpio4/direction

while true;  do
 	trap 'echo "4" > /sys/class/gpio/unexport' 0
	stat=`cat /sys/class/gpio/gpio4/value`
	while [ $stat = "1" ]
	do
			d=`date +%d%m%y`
			t=`date +%T`
			
			raspistill -vf -hf -o $t$d.jpg -w 1024 -h 768 -q 30
			echo "Movement Sensor Triggered $t $d" | mail -s "Movement Sensor Triggered" richardparker590@gmail.com
			echo "Movement Sensor Triggered $t $d"
			
			#run alarm program
			sudo python alarm.py
					
			#run python script to upload pic to aws S3 bucket
			sudo python uploader.py $d $t 
				
			#Email photo to customer address	
			mpack -s "Movement Sensor Photo" $t$d.jpg richardparker590@gmail.com
			stat="0"
			sleep 20
	done
done
exit 0