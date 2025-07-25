package de.omagh.core_data.repository.firebase;

import static org.junit.Assert.*;

import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_domain.model.Plant;

/**
 * Tests mapping of Firestore documents to model objects.
 */
public class FirestoreDaoMappingTest {

    @Test
    public void plantDao_fromDoc_mapsFields() throws Exception {
        DocumentSnapshot doc = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc.getString("id")).thenReturn("1");
        Mockito.when(doc.getString("name")).thenReturn("Rose");
        Mockito.when(doc.getString("type")).thenReturn("Flower");
        Mockito.when(doc.getString("imageUri")).thenReturn("img");
        Mockito.when(doc.getLong("updatedAt")).thenReturn(5L);

        FirestorePlantDao dao = new FirestorePlantDao("uid");
        Method m = FirestorePlantDao.class.getDeclaredMethod("fromDoc", DocumentSnapshot.class);
        m.setAccessible(true);
        Plant result = (Plant) m.invoke(dao, doc);

        assert result != null;
        assertEquals("1", result.getId());
        assertEquals("Rose", result.getName());
        assertEquals("Flower", result.getType());
        assertEquals("img", result.getImageUri());
        assertEquals(5L, result.getUpdatedAt());
    }

    @Test
    public void diaryDao_fromDoc_handlesNullTimestamp() throws Exception {
        DocumentSnapshot doc = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc.getString("id")).thenReturn("d1");
        Mockito.when(doc.getString("plantId")).thenReturn("p1");
        Mockito.when(doc.getLong("timestamp")).thenReturn(null);
        Mockito.when(doc.getString("note")).thenReturn("note");
        Mockito.when(doc.getString("imageUri")).thenReturn("img");
        Mockito.when(doc.getString("eventType")).thenReturn("watering");

        FirestoreDiaryEntryDao dao = new FirestoreDiaryEntryDao("uid");
        Method m = FirestoreDiaryEntryDao.class.getDeclaredMethod("fromDoc", DocumentSnapshot.class);
        m.setAccessible(true);
        DiaryEntry entry = (DiaryEntry) m.invoke(dao, doc);

        assert entry != null;
        assertEquals(0L, entry.getTimestamp());
        assertEquals("d1", entry.getId());
        assertEquals("p1", entry.getPlantId());
        assertEquals("note", entry.getNote());
        assertEquals("img", entry.getImageUri());
        assertEquals("watering", entry.getEventType());
    }
}
