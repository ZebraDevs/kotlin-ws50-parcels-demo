![ic_launcher](https://user-images.githubusercontent.com/6454841/201381082-ec4486ab-2bae-450a-8b4d-2ccf25dfc2c6.png)
# WS50 Parcels Demo Application

Demo Application which can be used to demonstrate the scanning capabilities of the WS50 in an environment such as a Warehouse one, where the worker has to scan the barcodes of the parcels and put them into the correct container locations.
The normal workflow of the application would be as follow:

![Parcels-Demo-Diagram](https://user-images.githubusercontent.com/6454841/201375803-90c09808-383b-4dc4-8d01-67b76737088b.png)

## Features

- Written in Kotlin
- Ability to import personalised samples data
    - Parcels Data as CSV File
    - Container Locations as JSON File
- "Manage External Storage" permission automatically pre-granted by using EMDK
- DataWedge Profile created and imported automatically by using EMDK
- Reports Screen
    - Check the amount of scanned parcels per each container location
    - Check in a specific container location the scanned barcodes
    - Search through the scanned barcodes of a container location by using the scanner or by typing it with the Keyboard
- Settings Screen
    - Ability to change AIM Type for the Scanner
        - Trigger
        - Press & Release
        - Presentation
    - Set AIM Timer
    - Set Same Barcode Timeout threshold
    - Enable/Disable Successful Confirmation Dialog while scanning
    - Reset current report session
    - Re-import container locations data
    - Re-import parcels data

## Setup & File Formats

The application already has samples data when installed for the first time and those can be used out of the box without making any changes.
You can checkout the files in these locations of the project:

```text
/app/src/main/assets/container_locations_sample.json
/app/src/main/assets/parcels_list_sample.csv
```

In case you want to use personalized data, you will have to use the same format which was used for the samples data contained in the project.
You will then, have to put the files on the external storage of the WS50 in this precise folder: ```ws50-parcels-demo```.
The files names will be different this time and they will not be the same as the ones used in the project but they will like this:

```text
/sdcard/ws50-parcels-demo/container_locations.json
/sdcard/ws50-parcels-demo/parcels_list.json
```

# License

[MIT](LICENSE.txt)