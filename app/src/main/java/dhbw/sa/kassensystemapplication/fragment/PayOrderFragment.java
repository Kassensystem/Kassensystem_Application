package dhbw.sa.kassensystemapplication.fragment;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import dhbw.sa.kassensystemapplication.Entity;
import dhbw.sa.kassensystemapplication.MainActivity;
import dhbw.sa.kassensystemapplication.R;
import dhbw.sa.kassensystemapplication.entity.Item;
import dhbw.sa.kassensystemapplication.entity.OrderedItem;

import static dhbw.sa.kassensystemapplication.MainActivity.widthPixels;

/**
 * A simple {@link Fragment} subclass.
 */
public class PayOrderFragment extends Fragment {

    //Nodes:
    private CheckBox payAll;
    private CheckBox printerService;
    private TextView priceToPay;
    private Button payOrderButton;

    //variables
    private int sizeOfRelativeLayout = 0;
    public static ArrayList<String> namesFromItems = new ArrayList<>();
    public static double storeOfSum = 0;
    public static String text = null;
    private double savePriceSeperat;
    private double savePriceFull;

    @SuppressLint("ValidFragment")

    public PayOrderFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pay_order, container, false);

        payAll = v.findViewById(R.id.payAllcheckBox);
        printerService = v.findViewById(R.id.printerServicecheckBox);
        priceToPay = v.findViewById(R.id.priceToPay);
        payOrderButton = v.findViewById(R.id.payOrderedItemsButton);

        // declare the universal pixels
        final int pix = (int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, 10,
                this.getResources().getDisplayMetrics());
        float posY = pix;

        for(OrderedItem orderedItem: MainActivity.orderedItems){

            if(!orderedItem.isItemPaid()){

                for (Item item: MainActivity.allItems){

                    if(item.getItemID() == orderedItem.getItemID()){

                        storeOfSum = storeOfSum + (item.getRetailprice());
                        storeOfSum = (double) ((int) storeOfSum + (Math.round(Math.pow(10, 3) * (storeOfSum - (int) storeOfSum))) / (Math.pow(10, 3)));

                        savePriceFull = storeOfSum;

                    }
                }

            }

        }

        priceToPay.setText(Double.toString(storeOfSum)+" €");
        priceToPay.setTextColor(Color.RED);
        priceToPay.setTextSize((float)(pix*1.5));

        RelativeLayout relativeLayout = v.findViewById(R.id.rlPayOrder);
        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();

        ScrollView scrollView = (ScrollView) v.findViewById(R.id.payAllScrollView);
        ViewGroup.LayoutParams paramsScrollView = scrollView.getLayoutParams();
        paramsScrollView.height = (MainActivity.heigthPixels)-(22*pix);


        /************************* Start der Forschleife für die Anzeige der Nodes***/

        for(OrderedItem orderedItem: MainActivity.orderedItems) {

            if (!orderedItem.isItemPaid()) {
                int sumOfItemIDsInOrder = 0;
                int itemID = orderedItem.getItemID();
                String name = null;

                //Get the right sum of orderedItems:
                for(OrderedItem item: MainActivity.orderedItems){
                    if(itemID == item.getItemID()){

                        if (!item.isItemPaid()) {
                            sumOfItemIDsInOrder++;
                        }

                    }
                }

                //Get the name of the Item:
                for (Item item : MainActivity.allItems) {

                    if (itemID == item.getItemID()) {
                        name = item.getName();
                        break;
                    }

                }

                if (!isItemAlreadySelected(name)) {

                    namesFromItems.add(name);

                    final TextView nameTextView = new TextView(getActivity());
                    final TextView quantityTextField = new TextView(getActivity());
                    final TextView inventoryTextView = new TextView(getActivity());
                    Button plus = new Button(getActivity());
                    Button minus = new Button(getActivity());

                    // Params for the TextView txt
                    nameTextView.setLayoutParams(new LinearLayout.LayoutParams(30 * pix, 10 * pix));
                    nameTextView.setText(name);
                    nameTextView.setX(pix*2);
                    nameTextView.setY(posY);
                    nameTextView.setPadding(pix, pix, pix, pix);
                    relativeLayout.addView(nameTextView);

                    // Params for the TextView inventory
                    inventoryTextView.setLayoutParams(new LinearLayout.LayoutParams(30*pix,10*pix));
                    inventoryTextView.setText(Integer.toString(sumOfItemIDsInOrder));
                    inventoryTextView.setX(widthPixels-15*pix-pix/2);
                    inventoryTextView.setY(posY);
                    inventoryTextView.setPadding(pix,pix,pix,pix);
                    relativeLayout.addView(inventoryTextView);

                    // Params for the TextView quantityTextField
                    quantityTextField.setLayoutParams(new LinearLayout.LayoutParams(8 * pix, 10 * pix));
                    quantityTextField.setText("0");
                    quantityTextField.setX(pix/10);
                    quantityTextField.setY(posY);
                    quantityTextField.setPadding(pix, pix, pix, pix);
                    relativeLayout.addView(quantityTextField);

                    // Set the Parameter for the Buttons plus and minus
                    // Params for the Button: +
                    plus.setLayoutParams(new LinearLayout.LayoutParams(4 * pix, 4 * pix));
                    plus.setText("+");
                    plus.setX(widthPixels - 10*pix);
                    plus.setPadding(pix, pix, pix, pix);
                    plus.setY(posY);
                    relativeLayout.addView(plus);

                    //Params for the Button: -
                    minus.setLayoutParams(new LinearLayout.LayoutParams(4 * pix, 4 * pix));
                    minus.setText("-");
                    minus.setX(MainActivity.widthPixels-6*pix);
                    minus.setPadding(pix, pix, pix, pix);
                    minus.setY(posY);
                    relativeLayout.addView(minus);

                    //The Y-position need to crow with the quantityTextField of Items!
                    posY = posY + 5 * pix;

                    /************************* PLus Und Minus Botton **********/

                    plus.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (payAll.isChecked()) {
                                if(Integer.parseInt((String)inventoryTextView.getText())>0) {

                                    // Update the quantity TextView
                                    int selectedQuantity = Integer.parseInt((String)quantityTextField.getText());
                                    selectedQuantity++;

                                    // Update the inventory TextView
                                    int numberOfInventory = Integer.parseInt((String) inventoryTextView.getText());
                                    numberOfInventory--;

                                    // Calculate the Sum
                                    double result = updateSum(true,(String)nameTextView.getText());

                                    // Set the updated quantity and inventory
                                    quantityTextField.setText(Integer.toString(selectedQuantity));
                                    inventoryTextView.setText(Integer.toString(numberOfInventory));

                                    // Set the updated Sum
                                    priceToPay.setText(Double.toString(result) + " €");
                                    if(payAll.isChecked()){

                                        savePriceSeperat = result;

                                    }

                                }
                            }
                        }

                    });

                    //Listener for the Button: - (The selected item will be remove from the order)
                    minus.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (payAll.isChecked()) {
                                //Get the quantity and the inventory
                                int selectedQuantity = Integer.parseInt((String) quantityTextField.getText());
                                int numberOfInventory = Integer.parseInt((String)inventoryTextView.getText());

                                // request if there is an Item in the Order
                                if (selectedQuantity > 0) {

                                    selectedQuantity--;
                                    numberOfInventory++;

                                    // Update the sumTextView and set the TextView Sum
                                    double result = updateSum(false, (String)nameTextView.getText());
                                    priceToPay.setText(Double.toString(result) + " €");

                                    if(payAll.isChecked()){
                                        savePriceSeperat = result;
                                    }

                                }

                                // Set the TextViews quantity and inventory
                                inventoryTextView.setText(Integer.toString(numberOfInventory));
                                quantityTextField.setText(Integer.toString(selectedQuantity));
                            }

                        }

                    });


                }
            }

        }

        //declare the length of the relativLayout
        for(String name: namesFromItems){

            sizeOfRelativeLayout++;

        }
        params.height = sizeOfRelativeLayout*5*pix;

        payOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!payAll.isChecked()) {

                    for (OrderedItem item: MainActivity.orderedItems){

                        item.setItemIsPaid(true);

                    }

                }

                if(printerService.isChecked()){
                    new PrintSalesCheck().execute();
                }

                new UpdateOrder().execute();

            }
        });

        //Auf Zugriff auf CheckBox warten
        payAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!payAll.isChecked()){

                    storeOfSum = savePriceFull;
                    priceToPay.setText(storeOfSum+" €");


                } else {
                    storeOfSum = savePriceSeperat;
                    priceToPay.setText(storeOfSum+" €");
                }

            }
        });

        return v;
    }

    private static boolean isItemAlreadySelected(String selectedName){

        for(String name: namesFromItems){
            if(name.equals(selectedName)){
                return true;
            }
        }

        return false;
    }

    private double updateSum(boolean isAdd, String itemName){


        for(Item item: MainActivity.allItems){

            if(item.getName().equals(itemName)){

                if(isAdd){

                    for (OrderedItem orderedItem: MainActivity.orderedItems){

                        if (orderedItem.getItemID() == item.getItemID()){

                            if (!orderedItem.isItemPaid()) {
                                storeOfSum = storeOfSum + (item.getRetailprice());
                                storeOfSum = (double) ((int) storeOfSum + (Math.round(Math.pow(10, 3) * (storeOfSum - (int) storeOfSum))) / (Math.pow(10, 3)));
                                //MainActivity.orderedItems.remove(orderedItem);
                                orderedItem.setItemIsPaid(true);
                                //MainActivity.orderedItems.add(orderedItem);
                                return storeOfSum;
                            }
                        }

                    }
                } else {


                    for (OrderedItem orderedItem: MainActivity.orderedItems){

                        if (orderedItem.getItemID() == item.getItemID() && orderedItem.isItemPaid()){

                            storeOfSum = storeOfSum - (item.getRetailprice());
                            storeOfSum = (double) ((int) storeOfSum + (Math.round(Math.pow(10, 3) * (storeOfSum - (int) storeOfSum))) / (Math.pow(10, 3)));
                            //MainActivity.orderedItems.remove(orderedItem);
                            orderedItem.setItemIsPaid(false);
                            //MainActivity.orderedItems.add(orderedItem);
                            return storeOfSum;
                        }

                    }

                }

            }

        }

        return storeOfSum;

    }

    private class UpdateOrder extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();

            try {

                ResponseEntity<Integer> responseEntity = restTemplate.exchange
                        (MainActivity.url + "/orderedItem", HttpMethod.PUT,
                                Entity.getEntity(MainActivity.orderedItems),Integer.class );


                MainActivity.orderedItems.clear();
                namesFromItems.clear();
                storeOfSum = 0;
                text = null;

            } catch (HttpClientErrorException e){
                text = e.getResponseBodyAsString();
                e.printStackTrace();
                namesFromItems.clear();
                return null;
            }catch (Exception e){
                text = "undefinierter Fehler";
                namesFromItems.clear();
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (text == null){
                showTableFragment();
            } else {
                showToast(text);
            }

        }


    }

    private class PrintSalesCheck extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();

            try {
                //Den Bon Ausdrucken
                ResponseEntity<Integer> responseEntity = restTemplate.exchange
                        (MainActivity.url + "/printOrder/"+MainActivity.selectedOrderID, HttpMethod.POST,
                                Entity.getEntity(null),Integer.class);

            } catch (HttpClientErrorException e){
                text = e.getResponseBodyAsString();
                e.printStackTrace();
            }catch (Exception e){
                text = "undefinierter Fehler";
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            showToast(text);

        }


    }

    private void showToast(String text){

        if(text != null){
            Toast.makeText(MainActivity.context, text, Toast.LENGTH_SHORT).show();
            this.text = null;
        }


    }

    private void showTableFragment(){

        TableSelectionFragment fragment = new TableSelectionFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }

}

