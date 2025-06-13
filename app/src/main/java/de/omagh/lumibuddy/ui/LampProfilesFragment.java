package de.omagh.lumibuddy.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

import de.omagh.lumibuddy.R;
import de.omagh.lumibuddy.feature_growlight.LampProduct;
import de.omagh.lumibuddy.data.model.GrowLightProduct;

/**
 * Screen for managing grow light profiles.
 */
public class LampProfilesFragment extends Fragment {
    private LampProfilesViewModel viewModel;
    private LampProfileAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lamp_profiles, container, false);

        RecyclerView rv = view.findViewById(R.id.lampProfilesRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LampProfileAdapter(new ArrayList<>());
        rv.setAdapter(adapter);
        View searchBtn = view.findViewById(R.id.searchProductsButton);

        adapter.setOnLampClickListener(lamp -> {
            viewModel.selectProfile(lamp);
            Toast.makeText(getContext(), "Selected " + lamp.name, Toast.LENGTH_SHORT).show();
        });

        adapter.setOnLampDeleteListener(lamp -> new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete")
                .setMessage("Delete " + lamp.name + "?")
                .setPositiveButton("Delete", (d, w) -> viewModel.removeProfile(lamp))
                .setNegativeButton("Cancel", null)
                .show());

        adapter.setOnLampEditListener(this::showEditDialog);

        FloatingActionButton fab = view.findViewById(R.id.addLampProfileFab);
        fab.setOnClickListener(v -> showAddDialog());
        searchBtn.setOnClickListener(v -> showSearchDialog());

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LampProfilesViewModel.class);

        viewModel.getProfiles().observe(getViewLifecycleOwner(), adapter::update);

        return view;
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_lamp_profile, null);
        EditText name = dialogView.findViewById(R.id.editLampName);
        EditText brand = dialogView.findViewById(R.id.editLampBrand);
        EditText type = dialogView.findViewById(R.id.editLampType);
        EditText spec = dialogView.findViewById(R.id.editLampSpectrum);
        EditText watt = dialogView.findViewById(R.id.editLampWattage);
        EditText factor = dialogView.findViewById(R.id.editLampFactor);
        EditText ppfd = dialogView.findViewById(R.id.editLampPPFD);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Add Lamp")
                .setView(dialogView)
                .setPositiveButton("Add", (d, w) -> {
                    try {
                        String id = UUID.randomUUID().toString();
                        int wv = Integer.parseInt(watt.getText().toString());
                        float f = Float.parseFloat(factor.getText().toString());
                        float p = Float.parseFloat(ppfd.getText().toString());
                        LampProduct lp = new LampProduct(id, name.getText().toString(), brand.getText().toString(),
                                type.getText().toString(), spec.getText().toString(), wv, f, p);
                        viewModel.addProfile(lp);
                    } catch (NumberFormatException ex) {
                        Toast.makeText(getContext(), "Invalid number", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(LampProduct lamp) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_lamp_profile, null);
        EditText name = dialogView.findViewById(R.id.editLampName);
        EditText brand = dialogView.findViewById(R.id.editLampBrand);
        EditText type = dialogView.findViewById(R.id.editLampType);
        EditText spec = dialogView.findViewById(R.id.editLampSpectrum);
        EditText watt = dialogView.findViewById(R.id.editLampWattage);
        EditText factor = dialogView.findViewById(R.id.editLampFactor);
        EditText ppfd = dialogView.findViewById(R.id.editLampPPFD);

        name.setText(lamp.name);
        brand.setText(lamp.brand);
        type.setText(lamp.type);
        spec.setText(lamp.spectrum);
        watt.setText(String.valueOf(lamp.wattage));
        factor.setText(String.valueOf(lamp.calibrationFactor));
        ppfd.setText(String.valueOf(lamp.ppfdAt30cm));

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.edit_lamp)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (d, w) -> {
                    try {
                        int wv = Integer.parseInt(watt.getText().toString());
                        float f = Float.parseFloat(factor.getText().toString());
                        float p = Float.parseFloat(ppfd.getText().toString());
                        LampProduct updated = new LampProduct(lamp.id, name.getText().toString(), brand.getText().toString(),
                                type.getText().toString(), spec.getText().toString(), wv, f, p);
                        viewModel.updateProfile(updated);
                    } catch (NumberFormatException ex) {
                        Toast.makeText(getContext(), R.string.invalid_number, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showSearchDialog() {
        final EditText query = new EditText(getContext());
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Search Lamps")
                .setView(query)
                .setPositiveButton("Search", (d, w) -> {
                    String q = query.getText().toString();
                    viewModel.searchProducts(q).observe(this, products -> {
                        if (products == null || products.isEmpty()) {
                            Toast.makeText(getContext(), "No results", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String[] names = new String[products.size()];
                        for (int i = 0; i < products.size(); i++)
                            names[i] = products.get(i).getModel();
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("Results")
                                .setItems(names, (dd, which) -> {
                                    GrowLightProduct p = products.get(which);
                                    LampProduct lp = new LampProduct(p.getId(), p.getModel(), p.getBrand(), p.getSpectrumType(), p.getSpectrum(), p.getWattage(), 1f, p.getPpfd());
                                    viewModel.addProfile(lp);
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
