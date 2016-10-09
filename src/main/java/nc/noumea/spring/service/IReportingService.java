package nc.noumea.spring.service;

public interface IReportingService {

	byte[] getCertificatAptitudePDF(String idVm) throws NumberFormatException, Exception;
}
