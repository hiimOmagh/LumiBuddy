package de.omagh.core_data.repository;

import de.omagh.core_data.db.LightCorrectionDao;
import de.omagh.core_data.model.LightCorrectionEntity;

/**
 * Repository wrapper over {@link LightCorrectionDao}.
 * Measurement screens use this to persist calibration
 * factors for different light types.
 */
public class LightCorrectionRepository {
    private final LightCorrectionDao dao;

    public LightCorrectionRepository(LightCorrectionDao dao) {
        this.dao = dao;
    }

    /**
     * Returns the stored correction factor or 1f if none exists.
     */
    public float getFactor(String type) {
        Float f = dao.getFactor(type);
        return f != null ? f : 1f;
    }

    /**
     * Saves the correction factor for the light type.
     */
    public void setFactor(String type, float factor) {
        dao.insert(new LightCorrectionEntity(type, factor));
    }
}
