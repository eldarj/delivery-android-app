package com.eldar.fit.seminarski.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eldar.fit.seminarski.R;
import com.eldar.fit.seminarski.data.Korpa;
import com.eldar.fit.seminarski.data.KorpaHranaStavka;
import com.eldar.fit.seminarski.helper.MySession;

public class KorpaFragment extends Fragment {

    private Korpa korpa;
    private TextView textKorpaIntro, textKorpaTotal;
    private ListView listKorpaStavke;
    private Button btnKorpaNaruci;
    private ImageButton btnKorpaOdbaci;

    public static KorpaFragment newInstance() {
        Bundle args = new Bundle();
        KorpaFragment fragment = new KorpaFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        ((RestoranDetaljnoActivity) getActivity()).getSupportActionBar().hide();
//        Log.i("test", "hide");

        getKorpaSession();

        super.onCreate(savedInstanceState);
    }

    private void getKorpaSession() {
        if (MySession.getKorpa() == null) {
            korpa = new Korpa();
            MySession.setKorpa(korpa);
        }
        korpa = MySession.getKorpa();
    }

    @Override
    public void onResume() {
        getKorpaSession();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_korpa, container, false);

        textKorpaIntro = view.findViewById(R.id.textKorpaIntro);
        textKorpaIntro.setText(korpa.getHranaStavke().size() == 0 ?
                "Korpa je prazna. Pregledajte restorane i jelovnike, izaberite i dodajte nešto u korpu!" :
                "Ukupno stavki u korpi: " + korpa.getHranaStavke().size());

        textKorpaTotal = view.findViewById(R.id.textKorpaTotal);
        final double ukupno = korpa.getUkupnaCijena();
        textKorpaTotal.setText(ukupno == 0 ? "- KM" : ukupno + " KM");

        listKorpaStavke = view.findViewById(R.id.listKorpaStavke);
        listKorpaStavkePopuni();

        btnKorpaOdbaci = view.findViewById(R.id.btnKorpaOdbaci);
        btnKorpaOdbaci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Korpa.izvrsiNarudzbu();
            }
        });

        btnKorpaNaruci = view.findViewById(R.id.btnKorpaNaruci);
        btnKorpaNaruci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Korpa.odbaciNarudzbu();
            }
        });

        return view;
    }

    private void listKorpaStavkePopuni() {
        // podaci -> korpa.getHranaStavke()
        BaseAdapter listStavkeAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return korpa.getHranaStavke().size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    view = inflater != null ? inflater.inflate(R.layout.stavka_korpa, parent, false) : null;
                }

                KorpaHranaStavka stavka = korpa.getHranaStavke().get(position);

                TextView textStavkaKorpaSuper = view.findViewById(R.id.textStavkaKorpaSuper);
                textStavkaKorpaSuper.setText("Restoran " + stavka.getHranaItemVM().getRestoran().getNaziv());

                TextView textStavkaKorpaNaziv = view.findViewById(R.id.textStavkaKorpaNaziv);
                textStavkaKorpaNaziv.setText("x" + stavka.getKolicina() + " " + stavka.getHranaItemVM().getNaziv());

                TextView textStavkaKorpaOpis = view.findViewById(R.id.textStavkaKorpaOpis);
                textStavkaKorpaOpis.setText("Cijena " + stavka.getHranaItemVM().getCijena());

                TextView textStavkaKorpaCijena = view.findViewById(R.id.textStavkaKorpaCijena);
                textStavkaKorpaCijena.setText(stavka.getUkupnaCijena() == 0 ? "- KM" : stavka.getUkupnaCijena() + " KM");

                ImageView imageStavkaKorpa = view.findViewById(R.id.imageStavkaKorpa);
                Glide.with(getActivity())
                        .load(stavka.getHranaItemVM().getImageUrl())
                        .centerCrop()
                        .into(imageStavkaKorpa);

                return view;
            }
        };

        listKorpaStavke.setAdapter(listStavkeAdapter);
    }
}
