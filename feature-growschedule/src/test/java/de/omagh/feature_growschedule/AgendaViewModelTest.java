package de.omagh.feature_growschedule;

import static org.junit.Assert.*;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.feature_growschedule.AgendaViewModel;

import org.junit.Test;

import java.util.List;

public class AgendaViewModelTest {
    @Test
    public void getAgendaEntries_initiallyEmpty() {
        AgendaViewModel vm = new AgendaViewModel();
        List<DiaryEntry> entries = vm.getAgendaEntries().getValue();
        assertNotNull(entries);
        assertTrue(entries.isEmpty());
    }
}
