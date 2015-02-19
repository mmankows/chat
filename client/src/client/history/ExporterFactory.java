package client.history;

public class ExporterFactory {
	public static Exporter createExporter(String type) {
		switch (type) {
		case "csv":
			return new CSVExporter();
		case "json":
			return new JSONExporter();
		case "html":
			return new HTMLExporter();
		default:
			throw new IllegalArgumentException("No factory for file of type'"
					+ type + "'");
		}
	}
}
