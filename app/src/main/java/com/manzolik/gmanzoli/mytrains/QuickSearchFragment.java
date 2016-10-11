package com.manzolik.gmanzoli.mytrains;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manzolik.gmanzoli.mytrains.components.FindTrainFragment;
import com.manzolik.gmanzoli.mytrains.components.TrainStatusFragment;
import com.manzolik.gmanzoli.mytrains.data.Station;


// Todo: capire perché il back-stack ha dei problemi (indietro non funziona)

public class QuickSearchFragment extends Fragment
        implements FindTrainFragment.OnTrainFoundListener {

    public QuickSearchFragment() {
        // Required empty public constructor
    }


    public static QuickSearchFragment newInstance() {
        QuickSearchFragment fragment = new QuickSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quick_search, container, false);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FindTrainFragment fragment = FindTrainFragment.newInstance();
        fragment.setOnTrainSelectedListener(this);

        //Replace fragment
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.quick_search_fragment_main_frame, fragment);
        ft.commit();

        return view;
    }

    @Override
    public void onTrainFound(int trainCode, Station departureStation) {
        System.out.println("TRAIN CODE: " + trainCode);
        // Metodo invocato quando l'utente ha scelto correttamente il treno
        // mostra il frammento successivo
        Fragment fragment = TrainStatusFragment.newInstance(Integer.toString(trainCode), departureStation);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.quick_search_fragment_main_frame, fragment)
                // Add this transaction to the back stack
                .addToBackStack(null)
                .commit();
    }
}
