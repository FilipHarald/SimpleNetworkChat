package server;


public interface LogListener {
    void onInit();
    void onWrite(String str);
    void onWriteFailed(String str);
    void onClose();
}
