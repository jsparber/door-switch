# Door Switch
A simple Android app to control a Sonoff wireless switch, without the need of custom firmware or DIY-mode.

I used a [1 Channel Ewelink Wifi RF Switch Module Self-Locking](https://it.aliexpress.com/item/1005005235671347.html) device bought on AliExpress.
Special thanks to [@AlexxIT et al.](https://github.com/AlexxIT/SonoffLAN) for the relevant code for interaction with the device.



## Setup
1. Put the device in pair mode, by long pressing for 5 secs the manual switch button after boot. If the device was already set up you need to press the button for 5 seconds to reset the device and then 5 seconds to enter pair mode
2. Connect to the new wifi access point called `ITEAD-*`
3. To talk to the device we need the device key
```wget -qO- http://10.10.7.1/device```
returns a JSON containing a field `apikey`, this is the `device key` needed in step 6.
4. Set the access point the device should connect with the following command. Make sure to set your WIFI credentials, unfortunately we can't use a custom server, therefore the switch will only work from the local network 
```wget -O- --post-data='{"version":4,"ssid":"XXXXXXX","password":"XXXXXXXX","serverName":"doesnotmatter","port":8000}' --header=Content-Type:application/json "http://10.10.7.1/ap"```
5. Build and install the app
6. Open `Door Switch Settings` and set the device IP, port (default: 8081) and the device key you got in step 3

## Further reading

- https://wiki.iteadstudio.com/PSF-B85
- https://github.com/itead/Sonoff_Devices_DIY_Tools
- https://wiki.almeroth.com/doku.php?id=projects:sonoff
- https://github.com/mdopp/simple-sonoff-server
- https://github.com/arendst/Tasmota/issues/453
- https://github.com/AlexxIT/SonoffLAN/

