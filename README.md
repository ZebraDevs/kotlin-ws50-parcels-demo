![ic_launcher](https://user-images.githubusercontent.com/6454841/201381082-ec4486ab-2bae-450a-8b4d-2ccf25dfc2c6.png)
# WS50 Parcels Demo Application

Demo Application which can be used to demonstrate the scanning capabilities of the WS50 in an environment such as a Warehouse one, where the worker has to scan the barcodes of the parcels and put them into the correct container locations.
The normal workflow of the application would be as follow:

![Parcels-Demo-Diagram](https://user-images.githubusercontent.com/6454841/201395580-45b68c7f-9adf-4b5f-b412-6a3cd8ece3ab.png)

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

# Screenshots

![Screenshot_20230217_160840](https://user-images.githubusercontent.com/6454841/219691632-a9d7f9df-ed09-49a4-859b-16031c3c09b7.png)
![Screenshot_20221109_160813](https://user-images.githubusercontent.com/6454841/201387767-4c002951-0d1f-44ab-a1f8-b33e3ed6fc83.png)
![Screenshot_20221109_160846](https://user-images.githubusercontent.com/6454841/201387763-592f2e33-d6bc-4ec8-bcce-a150b08e9cf1.png)
![Screenshot_20221109_160840](https://user-images.githubusercontent.com/6454841/201387765-0bb7c9db-a9f9-4a18-9455-1d874780d240.png)

![Screenshot_20221109_170337](https://user-images.githubusercontent.com/6454841/201387761-8575cbf2-bcce-4efb-8ef5-e93ee707ee49.png)
![Screenshot_20221109_170504](https://user-images.githubusercontent.com/6454841/201387759-45338d16-62f5-44a3-a785-161ea16dd336.png)
![Screenshot_20221109_170519](https://user-images.githubusercontent.com/6454841/201387758-8258948e-698e-4ba8-a4ee-9bff6ba37b62.png)
![Screenshot_20221109_170527](https://user-images.githubusercontent.com/6454841/201387749-0dfa0aed-9baf-4196-8a06-ac8068184d4a.png)

![Screenshot_20221109_160749](https://user-images.githubusercontent.com/6454841/201387773-708ba8fa-63ce-4c03-b906-0852213debf8.png)
![Screenshot_20221109_160709](https://user-images.githubusercontent.com/6454841/201387781-50622ae9-0c51-41be-adb3-adbd1458893f.png)
![Screenshot_20221109_160733](https://user-images.githubusercontent.com/6454841/201387778-241b63a4-7617-4dae-972e-75429a1dec7a.png)


# License

[MIT](LICENSE.txt)