package com.example.pethoalpar.zxingexample;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.datecs.api.printer.Printer;
import com.datecs.api.printer.ProtocolAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothPrinter extends Activity {

    final String CODIFICADOR_TEXTO = "ISO-8859-1";

    private BluetoothAdapter AdaptadorBT;
    private BluetoothDevice Dispositivo;
    private BluetoothSocket Conexion;
    //Datos de impresora
    private String printerName, statusPrinter;

    private Printer mPrinter;
    private ProtocolAdapter mProtocolAdapter;
    private ProtocolAdapter.Channel mPrinterChannel;
    //Pasar el nombre de la impresora
    public BluetoothPrinter(String NamePrinter){this.printerName = NamePrinter;}

    public void encontrarImpresora()
    {
        try
        {
            AdaptadorBT = BluetoothAdapter.getDefaultAdapter();

            if(AdaptadorBT == null){
                setStatusPrinter("No hay adaptador Bluetooth disponible");
                return;
            }

            Set<BluetoothDevice> dispositivosEmparejados = AdaptadorBT.getBondedDevices();

            if(dispositivosEmparejados.size() > 0)
            {
                for(BluetoothDevice dispositivo : dispositivosEmparejados)
                {
                    if(dispositivo.getName().equals(this.printerName))
                    {
                        Dispositivo = dispositivo;
                        break;
                    }
                }
            }

            if(Dispositivo != null)
                setStatusPrinter("Dispositivo encontrado");
            else
                setStatusPrinter("Dispositivo no encontrado");
        } catch(Exception e) {
            setStatusPrinter("");
        }
    }

    public void abrirConexionBluetooth() throws IOException
    {
        try {
            //--------------------------Identificador de Puerto Serial Estándar
            //--------------------------00001101-0000-1000-8000-00805f9b34fb
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            Conexion = Dispositivo.createRfcommSocketToServiceRecord(uuid);
            Conexion.connect();

            //mPrinter = new Printer(Conexion.getInputStream(),Conexion.getOutputStream());
            //comenzarRecibirDatos();

            initPrinter(Conexion.getInputStream(),Conexion.getOutputStream());

            if(mPrinter == null)
                setStatusPrinter("No se pudo conectar con impresora");
            else
                setStatusPrinter("Impresora conectada");
        }
        catch(Exception e) {
            setStatusPrinter("Error al conectar");
        }
    }

    public void initPrinter(InputStream inputStream, OutputStream outputStream) throws IOException
    {
        mProtocolAdapter = new ProtocolAdapter(inputStream, outputStream);

        if(mProtocolAdapter.isProtocolEnabled()) {
            mPrinterChannel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_PRINTER);
            mPrinter = new Printer(mPrinterChannel.getInputStream(), mPrinterChannel.getOutputStream());
        }else{
            mPrinter = new Printer(mProtocolAdapter.getRawInputStream(),mProtocolAdapter.getRawOutputStream());
        }
    }

    public void imprimirInfo(String infoImp)
    {
        if(mPrinter != null)
        {
            infoImp+="\n";

            try {
                mPrinter.reset();
                mPrinter.printText(infoImp,CODIFICADOR_TEXTO);
                mPrinter.flush();
            } catch (IOException e) {

            }
        }
    }

    public  void imprimirTextoFormateado(String infoImp)
    {
        try {
            mPrinter.reset();
            mPrinter.printTaggedText(infoImp,CODIFICADOR_TEXTO);
            mPrinter.flush();
        } catch (IOException e) {

        }
    }

    public void imprimirCodigoDeBarra(int tipo, String datos)
    {
        try {
            mPrinter.reset();
            mPrinter.setBarcode(Printer.ALIGN_CENTER, false, 2, Printer.HRI_BELOW, 100);
            mPrinter.printBarcode(tipo,datos);
            mPrinter.feedPaper(110);
            mPrinter.flush();
        } catch (IOException e) {

        }

    }

    public void imprimirImagen(String imagenName) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        AssetManager assetManager = getApplicationContext().getAssets();

        Bitmap bitmap = BitmapFactory.decodeStream(assetManager.open(imagenName), null, options);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] argb = new int[width * height];
        bitmap.getPixels(argb, 0, width, 0, 0, width, height);
        bitmap.recycle();

        mPrinter.reset();
        mPrinter.printCompressedImage(argb, width, height, Printer.ALIGN_CENTER, true);
        mPrinter.feedPaper(110);
        mPrinter.flush();

    }

    public void imprimirQRCode(int size,int n, String info)
    {
        try {
            mPrinter.reset();
            mPrinter.setBarcode(Printer.ALIGN_CENTER, false, 2, Printer.HRI_NONE, 100);
            mPrinter.printQRCode(size,n,info);
            mPrinter.feedPaper(110);
            mPrinter.flush();
        } catch (IOException e) {

        }
    }
    public void ponerBeep(){
        try {
            mPrinter.beep();
        } catch (IOException e) {

        }
    }
    public void ponerBeep(int numeroVeces){
        try {
            for(int i = 0; i < numeroVeces; i++) {
                mPrinter.beep();
            }
        } catch (IOException e) {

        }
    }
    public void cerrarConexionBluetooth() throws IOException
    {
        try
        {
            Conexion.close();
            setStatusPrinter("Conexion cerrada.");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public void setStatusPrinter(String statusPrinter) {
        this.statusPrinter = statusPrinter;
    }

    public String getStatusPrinter(){
        return this.statusPrinter;
    }

    public String getEnconding() {
        return CODIFICADOR_TEXTO;
    }
}
