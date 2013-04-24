package nc.mairie.spring.dao.mapper.metier.avancement;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.avancement.AvancementCapPrintJobDao;
import nc.mairie.spring.domain.metier.avancement.AvancementCapPrintJob;

import org.springframework.jdbc.core.ResultSetExtractor;

public class AvancementCapPrintJobResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		AvancementCapPrintJob avctJob = new AvancementCapPrintJob();
		avctJob.setIdAvancementCapPrintJob(rs.getInt(AvancementCapPrintJobDao.CHAMP_ID_AVCT_CAP_PRINT_JOB));
		avctJob.setIdAgent(rs.getInt(AvancementCapPrintJobDao.CHAMP_ID_AGENT));
		avctJob.setLogin(rs.getString(AvancementCapPrintJobDao.CHAMP_LOGIN));
		avctJob.setIdCap(rs.getInt(AvancementCapPrintJobDao.CHAMP_ID_CAP));
		avctJob.setCodeCap(rs.getString(AvancementCapPrintJobDao.CHAMP_CODE_CAP));
		avctJob.setIdCadreEmploi(rs.getInt(AvancementCapPrintJobDao.CHAMP_ID_CADRE_EMPLOI));
		avctJob.setLibCadreEmploi(rs.getString(AvancementCapPrintJobDao.CHAMP_LIB_CADRE_EMPLOI));
		avctJob.setEaes(rs.getBoolean(AvancementCapPrintJobDao.CHAMP_IS_EAES));
		avctJob.setDateSubmission(rs.getDate(AvancementCapPrintJobDao.CHAMP_DATE_SUBMISSION));
		avctJob.setDateStatut(rs.getDate(AvancementCapPrintJobDao.CHAMP_DATE_STATUT));
		avctJob.setStatut(rs.getString(AvancementCapPrintJobDao.CHAMP_STATUT));
		avctJob.setJobId(rs.getString(AvancementCapPrintJobDao.CHAMP_JOB_ID));

		return avctJob;
	}
}
