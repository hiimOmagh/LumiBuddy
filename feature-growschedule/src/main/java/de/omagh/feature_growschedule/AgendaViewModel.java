package de.omagh.feature_growschedule;

import androidx.lifecycle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;

import de.omagh.core_data.model.DiaryEntry;

public class AgendaViewModel extends ViewModel {
    private final MutableLiveData<List<DiaryEntry>> agendaEntries =
            new MutableLiveData<>(Collections.emptyList());

    public LiveData<List<DiaryEntry>> getAgendaEntries() {
        return agendaEntries;
    }
}
