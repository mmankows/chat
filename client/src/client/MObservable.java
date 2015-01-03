package client;

public interface MObservable {
	public void addObserwer(MObserver observer);
	public void removeObserver(MObserver observer);
	public void notifyObservers();
}
