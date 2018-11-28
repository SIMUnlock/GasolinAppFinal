package com.fuel.my.myfuel4;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class FavListFragment extends Fragment {
    private OnFavTouchedListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach (context);

        if (context instanceof OnFavTouchedListener) {
            listener = (OnFavTouchedListener) context;
        } else {
            throw new ClassCastException ("Must implement OnFavTouchedListener first!");
        }
    }

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate (R.layout.fragment_favs_list, container, false);
    }

    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);

        FragmentActivity activity = getActivity ();
        if (activity == null) return;


        ArrayList<Estaciones> estaciones = new ArrayList<>();

        for (int i=0; i <MapsActivity.myStations.size();i++){
            if(MainActivity.usuarioLogin.getFavoritos().containsKey(MapsActivity.myStations.get(i).getPlace_id())){
                estaciones.add(MapsActivity.myStations.get(i));
            }
        }

        RecyclerView recyclerView = activity.findViewById (R.id.recview);
        recyclerView.setLayoutManager (new LinearLayoutManager(getContext()));
        recyclerView.setAdapter (new FavListAdapter (getContext (), estaciones, listener));
    }
}

class FavListAdapter extends RecyclerView.Adapter<FavViewHolder> {
    private Context context;
    private ArrayList<Estaciones> favoritos;

    private OnFavTouchedListener listener;

    FavListAdapter (Context context, ArrayList<Estaciones> favoritos, OnFavTouchedListener listener) {
        this.context = context;
        this.favoritos=favoritos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder (@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from (context).inflate (R.layout.recycler_list_item, viewGroup, false);
        return new FavViewHolder (view);
    }

    @Override
    public void onBindViewHolder (@NonNull FavViewHolder memesViewHolder, int i) {
        ImageView corazon=memesViewHolder.bind (favoritos.get(i));

        memesViewHolder.itemView.setOnClickListener (view -> {
            if (listener != null) listener.onFavTouched (Integer.parseInt(favoritos.get(i).getPlace_id()),corazon);
        });
    }

    @Override
    public int getItemCount () {
        return favoritos.size();
    }
}

class FavViewHolder extends RecyclerView.ViewHolder {
    public static ImageView corazon;
    private TextView nombreEstacaion,regular,premium,diesel,update;

    FavViewHolder (@NonNull View itemView) {
        super (itemView);

        nombreEstacaion = itemView.findViewById (R.id.nombreFav);

        regular = itemView.findViewById (R.id.textMagnaFav);

        premium = itemView.findViewById (R.id.textPremiumFav);

        diesel = itemView.findViewById (R.id.textDieselFav);

        update = itemView.findViewById (R.id.textUpdateFav);

        corazon = itemView.findViewById(R.id.imageViewHeartFav);
    }

    ImageView bind (Estaciones estaciones) {

        nombreEstacaion.setText (estaciones.getNombre());
        regular.setText("$"+estaciones.getPrecio().getRegular());
        premium.setText("$"+estaciones.getPrecio().getPremium());
        diesel.setText("$"+estaciones.getPrecio().getPremium());
        update.setText("Actualización: "+estaciones.getPrecio().getActualizacion());
        corazon.setImageResource(R.drawable.heartselected);
        return corazon;
    }

}