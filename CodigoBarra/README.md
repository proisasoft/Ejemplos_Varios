<H1>Zxing example in Android Studio.</H1>

<H3>Zxing library helps to read a barcode.

To the build gradle you must add </H3>
```gradle
compile 'com.journeyapps:zxing-android-embedded:3.2.0@aar'
compile 'com.google.zxing:core:3.2.1'
```

<H3>To start the scanner, add this code.</H3>

```java
IntentIntegrator integrator = new IntentIntegrator(activity);
integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
integrator.setPrompt("Scan");
integrator.setCameraId(0);
integrator.setBeepEnabled(false);
integrator.setBarcodeImageEnabled(false);
integrator.initiateScan();
```

<H3>To obtain the result, add in onActivityResult.</H3>

```java
IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
if(result != null) {
   if(result.getContents() == null) {
       Log.d("MainActivity", "Cancelled scan");
       Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
    } else {
       Log.d("MainActivity", "Scanned");
       Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
    }
}
```
