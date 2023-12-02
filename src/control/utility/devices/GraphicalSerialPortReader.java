package control.utility.devices;

import view.main.panel.dialog.BarcodeReaderDialog;

public class GraphicalSerialPortReader extends BasicSerialPortReader {

    private final BarcodeReaderDialog view;

    private final Thread readAction;

    public GraphicalSerialPortReader(BarcodeReaderDialog view) {
        this.view = view;
        view.addBarcodeListener(this);
        readAction = new Thread(view::waitIdle);
    }

    @Override
    public void readSerialPort() {
        super.readSerialPort();
        readAction.start();
    }

    @Override
    public void readBarCode(String barCode) {
        System.out.println("Read barcode " + barCode);
        synchronized (this) {
            view.setVisible(false);
        }
        super.readBarCode(barCode);
    }
}
