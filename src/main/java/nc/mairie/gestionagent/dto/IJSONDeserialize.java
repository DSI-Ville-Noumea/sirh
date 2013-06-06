package nc.mairie.gestionagent.dto;

public interface IJSONDeserialize<T> {
	public T deserializeFromJSON(String json);
}
