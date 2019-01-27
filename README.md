We're talking about using a light ring to help our computer vision code identify targets.  The light ring will likely be connected to a Arduino and could be on at full brightness during the whole match.  Another option would be to have the light ring connected to the co-processor that is running the computer vision code and have the co-processor instruct the light ring when to 'flash'.  This code is intended to be an end-to-end demonstration but using blinks of a simple LED instead of a wohle light ring.

# Physical

This project assumes an Arduino Pro Micro is connected via USB to a Raspberry Pi.  The circuit diagram is shown below to set this up.

# Software

There are two bits of code written for this example: an Arduino app that drives a slow fade and listens on the Serial port for instructions and then a Raspberry Pi app that sends random numbers to the Arduino every 6 seconds.  When the Arduino gets a number over the serial port it blinks the LED the given number of times and then resumes the fade.

## Arduino

For the demo, I chose a Pro Micro 5v because I had one available.

The fade-listen/fade-listen.ino file contains the Arduino code.  This code started from http://www.arduino.cc/en/Tutorial/Fade and then had the Serial logic added.  The baud rate chosen was 57600 because 9600 felt too slow and some people were complaining that 115200 was unreliable on Arduino's.

Upload the code to an Arduino from the Arduino IDE.

## Raspberry Pi

For the demo, I chose a Raspberry Pi 3B because I had one available.

To prepare the Raspberry Pi the rxtx library needs to be installed.

$ sudo apt-get install librxtx-java

Once the library is installed the code can be compiled and run.

$ javac -cp /usr/share/java/RXTXcomm.jar FadeWriter.java 
$ java -Djava.library.path=/usr/lib/jni -cp /usr/share/java/RXTXcomm.jar:. FadeWriter

# Circuit

The cap and resistor on the RST are to prevent the Pro Micro from starting in ultra-mega slow mode when plugged in.  A blue LED was chosen set on pin 5 because 5 is a nice number.

# Output

The Arduino code runs automatically after upload and you'll know it is working because the LED will fade on and off.  The Raspberry Pi will show some output: it will send a random number to the Arduino and then, after the light blinks on the Arduino, it will print out the number of blinks that you should have seen.  The number is actually an echo back from the Arduino on the serial port.

```
$ java -Djava.library.path=/usr/lib/jni -cp /usr/share/java/RXTXcomm.jar:. FadeWriter
Looking for port /dev/ttyACM0
RXTX Warning:  Removing stale lock file. /var/lock/LCK..ttyACM0
Found port /dev/ttyACM0
Writing 3 to /dev/ttyACM0
Writing 5 to /dev/ttyACM0
Writing 3 to /dev/ttyACM0
^C
```
