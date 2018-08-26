# My Supermarket
"My Supermarket" is an Android application to provide a better shopping experience to customers in the buying process in supermarket stores, from building a wish list to checking out the picked up items in the store before they leave. 

This is the Capstone project for the Udacity Android Developer Nanodegree.

## Quick Start

### Preparation Step
This project uses Google Maps SDK for Android, which requires an API key. If you do not have one, please go to [Google Cloud Platform console](https://console.cloud.google.com) to get one.

### Cloning the project
After getting the API key, you may clone this project repository to your local directory:

```
git clone https://github.com/peterkwan/Udacity-Android-Capstone.git
```

### Add the API Key
To add the API key to the application, go to `<Project root directory>/app/src/main/res/values` and add the following in `strings.xml`:

```
<string name="google_maps_key" translatable="false">### Your API Key Goes Here###</string>
```

Replace `###Your API Key Goes Here###` with the API key obtained in Preparation Step.

### Build and Installation
You may build and install the application to your mobile device.  This project supports both debug and release build.  

For example, you may run `gradlew installRelease` in the project root directory to generate and install a release build to the connected Android device.

## Notes to the Testers
Due to the small dataset in the server database, this application may require an emulator to mock locations and test the shopping cart functionality. The dataset can be found [here](https://goo.gl/ibGPns).

## License
This project does not have any license yet.  
