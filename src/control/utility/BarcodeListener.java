package control.utility;

public interface BarcodeListener {
    /**
     * An event for incoming barcode for outer Barcode reader device
     * @param barCode the read data
     */
    void readBarCode(String barCode);
}
