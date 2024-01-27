package com.example.asadordelchapulin;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asadordelchapulin.encargado.AdapterCategorias;
import com.example.asadordelchapulin.encargado.Categoria;
import com.example.asadordelchapulin.encargado.Platillo;
import com.example.asadordelchapulin.encargado.Producto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GenerarOrdenFragment extends Fragment implements OnTaskCompleted {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;

    public static Comanda comanda;

    public static boolean docBarra;
    public static boolean docComal;
    private String mParam2;
    private View view;
    public static boolean modificacion;
    public static boolean correcion = false;
    private ArrayList<ProductoOrdenado> barra, cocina, comal;

    public static String mesa, mesero;
    private int cant = 1;
    private double totalComanda = 0;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ArrayList<ProductoOrdenado> listaProductosOrdenados;
    private LinearLayout linearLayoutProductosOrdenados;
    private Spinner categorias, productos;
    private TextView cantidad;
    private EditText descripcion;

    private AutoCompleteTextView buscador;

    private ArrayList<Platillo> resultados;
    private ArrayList<Platillo> milista;

    private ArrayAdapter<Platillo> adaptadorBusqueda;

    public static String cliente;
    public static AdapterCategorias adaptadorCategorias;

    private static SendNotification notification = new SendNotification();

    private Button realizarModificacion, generarOrden, addPlatillosCuenta;


    public GenerarOrdenFragment() {
        // Required empty public constructor
    }

    public static GenerarOrdenFragment newInstance(String param1, String param2) {
        GenerarOrdenFragment fragment = new GenerarOrdenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (comanda != null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("¿Desea descartar los cambios?").setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            comanda.getDocumentReference().update("estado", "en espera");
                            Toast.makeText(getContext(), "No se realizaron cambios", Toast.LENGTH_SHORT).show();
                            comanda = null;
                            Navigation.findNavController(view).popBackStack();
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog titulo = alert.create();
                    titulo.setTitle("Comanda en Modificación");
                    titulo.show();
                } else if (correcion) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("¿Desea salir?, la información se perderá").setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getContext(), "Correción descartada", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(view).popBackStack();
                        }
                    }).setNegativeButton("Volver", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog titulo = alert.create();
                    titulo.setTitle("Cancelar Acción");
                    titulo.show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("La Orden no se ha generado, ¿Desea salir?").setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getContext(), "Comanda descartada", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(view).popBackStack();
                        }
                    }).setNegativeButton("Volver", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog titulo = alert.create();
                    titulo.setTitle("Orden no Generada");
                    titulo.show();
                }

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_generar_orden, container, false);
        buscador = view.findViewById(R.id.buscador);
        resultados = new ArrayList<>();
        milista = new ArrayList<>();
        for (Categoria categoria : adaptadorCategorias.getCategorias()) {
            milista.addAll(categoria.getPlatillos());
        }
        ;

        adaptadorBusqueda = new ArrayAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, resultados);
        buscador.setAdapter(adaptadorBusqueda);
        buscador.setThreshold(1);
        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscarCoincidencias(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buscador.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Platillo platilloEncontrado = (Platillo) parent.getItemAtPosition(position);
                if(!platilloEncontrado.getArea().equals("comal")){
                    agregarProducto(new ProductoOrdenado(platilloEncontrado.getNombre(), platilloEncontrado.getPrecio(), platilloEncontrado.getArea(), 1, ""));
                }
                else{
                    ProductoOrdenado productoOrdenadoAntojito=new ProductoOrdenado("Del comal",platilloEncontrado.getPrecio(),"comal",1,1+" "+platilloEncontrado.getNombre());
                    productoOrdenadoAntojito.addProducto(new ProductoOrdenado(platilloEncontrado.getNombre(),platilloEncontrado.getPrecio(),platilloEncontrado.getArea(),1,""));
                    agregarProducto(productoOrdenadoAntojito);
                }
                buscador.setText("");
                buscador.clearFocus();
                ocultarTeclado();
            }
        });

        ((TextView) view.findViewById(R.id.tvTitleMesaGenerarOrden)).setText("Mesa " + mesa);
        ((TextView) view.findViewById(R.id.tvClienteGenerarOrden)).setText(cliente);
        descripcion = view.findViewById(R.id.etDescripcion);
        realizarModificacion = view.findViewById(R.id.btnRealizarModificacion);
        addPlatillosCuenta = view.findViewById(R.id.btnAddToCuenta);
        generarOrden = view.findViewById(R.id.btnGenerarOrden);
        listaProductosOrdenados = new ArrayList<>();
        linearLayoutProductosOrdenados = view.findViewById(R.id.contenedorProductosOrdenados);
        cantidad = view.findViewById(R.id.tvCantidad);
        cantidad.setText("" + cant);
        categorias = view.findViewById(R.id.spinnerCategoria);
        productos = view.findViewById(R.id.spinnerProducto);
        ArrayAdapter adapterCategorias = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, adaptadorCategorias.getNombresCategoria());
        categorias.setAdapter(adapterCategorias);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (modificacion) {
            realizarModificacion.setVisibility(View.VISIBLE);
            generarOrden.setVisibility(View.GONE);
        }
        if (correcion) {
            addPlatillosCuenta.setVisibility(View.VISIBLE);
            generarOrden.setVisibility(View.GONE);
        }
        if (comanda != null) {
            for (ProductoOrdenado productoOrdenado : comanda.getProductoOrdenados()) {
                agregarProducto(productoOrdenado);
                totalComanda += productoOrdenado.getCantidad() * productoOrdenado.getPrecio();
            }
        }

        categorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (categorias.getSelectedItem().toString().equals("+ Nuevo producto")) {
                    View dialogView = getLayoutInflater().inflate(R.layout.card_new_producto, null);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText iProducto = dialogView.findViewById(R.id.iNewProducto);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText iDescripcion = dialogView.findViewById(R.id.idescripcion);
                    RadioGroup radioGroup = dialogView.findViewById(R.id.radiogroup);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView cantidad = dialogView.findViewById(R.id.sCant);
                    ((Button) dialogView.findViewById(R.id.bdecrement)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!cantidad.getText().toString().equals("1")) {
                                cantidad.setText((Integer.parseInt(cantidad.getText().toString()) - 1) + "");
                            }
                        }
                    });
                    ((Button) dialogView.findViewById(R.id.bincrement)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cantidad.setText("" + (Integer.parseInt(cantidad.getText().toString()) + 1));
                        }
                    });
                    EditText precio = dialogView.findViewById(R.id.iPrecio);
                    ((Button) dialogView.findViewById(R.id.bAgregarNuevo)).setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("NonConstantResourceId")
                        @Override
                        public void onClick(View v) {
                            if (iProducto.getText().toString().equals("")) {
                                Toast.makeText(getContext(), "Ingrese nombre del producto", Toast.LENGTH_SHORT).show();
                                iProducto.requestFocus();
                            } else if (precio.getText().toString().equals("")) {
                                Toast.makeText(getContext(), "Ingrese el costo del platillo", Toast.LENGTH_SHORT).show();
                                precio.requestFocus();
                            } else {
                                String area = ((RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString().toLowerCase();
                                Toast.makeText(getContext(), area, Toast.LENGTH_SHORT).show();
                                agregarProducto(new ProductoOrdenado(iProducto.getText().toString(), Double.parseDouble(precio.getText().toString()), area, Integer.parseInt(cantidad.getText().toString()), iDescripcion.getText().toString()));
                                Toast.makeText(getContext(), "Producto añadido", Toast.LENGTH_SHORT).show();
                                iProducto.setText("");
                                iProducto.clearFocus();
                                precio.setText("");
                                precio.clearFocus();
                                cantidad.setText("1");
                                iDescripcion.setText("");
                                iDescripcion.clearFocus();
                            }
                        }
                    });

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false);
                    builder.setView(dialogView).setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            categorias.setSelection(0);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else if (categorias.getSelectedItem().toString().equals("Del comal")) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.card_del_comal, null);
                    Spinner spinnerAntojitos = dialogView.findViewById(R.id.spinerAntojitos);
                    TextView tvCantidadAntojitos = dialogView.findViewById(R.id.cantidadAntojito);
                    TextView tvCantidadPlatillos = dialogView.findViewById(R.id.cantidadPlatillo);
                    LinearLayout detallesAntojitos = dialogView.findViewById(R.id.detalleAntojitos);
                    EditText observacionesAntojito = dialogView.findViewById(R.id.observacionesPlatillo);
                    final ArrayList<ProductoOrdenado>[] productosAntojitos = new ArrayList[]{new ArrayList<>()};
                    final ArrayAdapter[] adapterPlatillos = {new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, adaptadorCategorias.getCategoria(categorias.getSelectedItemPosition()).getNombrePlatillos())};
                    adapterPlatillos[0].setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerAntojitos.setAdapter(adapterPlatillos[0]);
                    ((Button) dialogView.findViewById(R.id.btnMenosAntojito)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!tvCantidadAntojitos.getText().toString().equals("1")) {
                                tvCantidadAntojitos.setText(Integer.parseInt(tvCantidadAntojitos.getText().toString()) - 1 + "");
                            }
                        }
                    });

                    ((Button) dialogView.findViewById(R.id.btnMasAntojito)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tvCantidadAntojitos.setText(Integer.parseInt(tvCantidadAntojitos.getText().toString()) + 1 + "");
                        }
                    });

                    ((Button) dialogView.findViewById(R.id.btnMenosPlatillo)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!tvCantidadPlatillos.getText().toString().equals("1")) {
                                tvCantidadPlatillos.setText(Integer.parseInt(tvCantidadPlatillos.getText().toString()) - 1 + "");
                            }
                        }
                    });

                    ((Button) dialogView.findViewById(R.id.btnMasPlatillo)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tvCantidadPlatillos.setText(Integer.parseInt(tvCantidadPlatillos.getText().toString()) + 1 + "");
                        }
                    });

                    ((Button) dialogView.findViewById(R.id.addAntojito)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Platillo platillo = adaptadorCategorias.getCategoria(categorias.getSelectedItemPosition()).getPlatillo(spinnerAntojitos.getSelectedItemPosition());
                            ProductoOrdenado aux = new ProductoOrdenado(platillo.getNombre(), platillo.getPrecio(), "", Integer.parseInt(tvCantidadAntojitos.getText().toString()), "");
                            aux.setProducto(getNombrePlatilloFormateado(aux));
                            productosAntojitos[0].add(aux);
                            View cardProductoOrdenado = inflater.inflate(R.layout.card_producto_ordenado_si, null);
                            ((TextView) cardProductoOrdenado.findViewById(R.id.tvCantProduct)).setText(Integer.parseInt(tvCantidadAntojitos.getText().toString()) + "");
                            ((TextView) cardProductoOrdenado.findViewById(R.id.tvProductOrden)).setText(platillo.getNombre());
                            cardProductoOrdenado.setOnTouchListener(new View.OnTouchListener() {
                                private int lastAction;
                                private int initialX;
                                private int initialY;
                                private float initialTouchX;
                                private float initialTouchY;

                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    switch (event.getAction()) {
                                        case MotionEvent.ACTION_DOWN:
                                            // Guardar las coordenadas iniciales y la acción actual
                                            initialX = (int) v.getX();
                                            initialY = (int) v.getY();
                                            initialTouchX = event.getRawX();
                                            initialTouchY = event.getRawY();
                                            lastAction = MotionEvent.ACTION_DOWN;
                                            return true;
                                        case MotionEvent.ACTION_MOVE:
                                            // Calcular el desplazamiento
                                            int deltaXX = (int) (event.getRawX() - initialTouchX);
                                            int deltaYY = (int) (event.getRawY() - initialTouchY);

                                            // Actualizar la posición de la vista
                                            v.setX(initialX + deltaXX);
                                            v.setY(initialY + deltaYY);
                                            lastAction = MotionEvent.ACTION_MOVE;
                                            return true;
                                        case MotionEvent.ACTION_UP:
                                            int deltaX = (int) (event.getRawX() - initialTouchX);
                                            int deltaY = (int) (event.getRawY() - initialTouchY);

                                            // Calcular la distancia total movida
                                            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                                            // Si la distancia es mayor que cierto valor, realiza la acción
                                            if (distance > 40) { // Puedes ajustar el valor según tus necesidades
                                                // Realiza la acción cuando se ha movido lo suficiente
                                                // ...
                                                detallesAntojitos.removeView(cardProductoOrdenado);
                                                productosAntojitos[0].remove(aux);
                                                Toast.makeText(getContext(), "Producto eliminado ...", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Si no se movió lo suficiente, devuelve la vista a su posición original
                                                v.setX(initialX);
                                                v.setY(initialY);
                                            }
                                            return true;
                                    }
                                    return false;
                                }

                            });
                            detallesAntojitos.addView(cardProductoOrdenado);
                            tvCantidadAntojitos.setText("1");
                        }
                    });

                    ((Button) dialogView.findViewById(R.id.btnAddPlatilloAntojito)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (productosAntojitos[0].size() > 0) {
                                double precio = 0;
                                String descripcionAntojitos = "";
                                for (ProductoOrdenado product : productosAntojitos[0]) {
                                    precio += product.getCantidad() * product.getPrecio();
                                    descripcionAntojitos += product.getCantidad() + " " + product.getProducto() + ", ";
                                }
                                descripcionAntojitos = descripcionAntojitos.substring(0, descripcionAntojitos.length() - 2);
                                descripcionAntojitos += " " + observacionesAntojito.getText().toString();
                                ProductoOrdenado aux = new ProductoOrdenado("Del comal", precio, "comal", Integer.parseInt(tvCantidadPlatillos.getText().toString()), descripcionAntojitos);
                                agregarProducto(aux);
                                Toast.makeText(getContext(), "Platillo agregado", Toast.LENGTH_SHORT).show();
                                tvCantidadPlatillos.setText("1");
                                observacionesAntojito.setText("");
                                detallesAntojitos.removeAllViews();
                                productosAntojitos[0] = new ArrayList<>();
                            } else {
                                Toast.makeText(getContext(), "Platillo vacío", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                    // Crea el AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false);
                    builder.setView(dialogView)
                            //.setTitle("AlertDialog con Diseño Personalizado")
                            .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    categorias.setSelection(0);
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    ArrayAdapter adapterPlatillos = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, adaptadorCategorias.getCategoria(categorias.getSelectedItemPosition()).getNombrePlatillos());
                    adapterPlatillos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    productos.setAdapter(adapterPlatillos);
                    productos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Platillo platillo = adaptadorCategorias.getCategoria(categorias.getSelectedItemPosition()).getPlatillo(productos.getSelectedItemPosition());
                            if (!platillo.getExistencia()) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                alert.setMessage("El producto seleccionado no se encuentra en existencia").setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                AlertDialog titulo = alert.create();
                                titulo.setTitle("Producto sin existencia");
                                titulo.show();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ((Button) view.findViewById(R.id.btnMas)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cant++;
                cantidad.setText("" + cant);
            }
        });

        ((Button) view.findViewById(R.id.btnMenos)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cant > 1) {
                    cant--;
                    cantidad.setText("" + cant);
                }
            }
        });

        ((Button) view.findViewById(R.id.btnAgregarProducto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productos.getSelectedItem() != null) {
                    Platillo platillo = adaptadorCategorias.getCategoria(categorias.getSelectedItemPosition()).getPlatillo(productos.getSelectedItemPosition());
                    if (platillo.getExistencia()) {
                        ProductoOrdenado productoOrdenado = new ProductoOrdenado(platillo.getNombre(), platillo.getPrecio(), adaptadorCategorias.getCategoria(categorias.getSelectedItemPosition()).getArea(), cant, descripcion.getText().toString());
                        agregarProducto(productoOrdenado);
                        ocultarTeclado();
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setMessage("El producto seleccionado no se encuentra en existencia").setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog titulo = alert.create();
                        titulo.setTitle("Producto sin existencia");
                        titulo.show();
                    }

                } else {
                    Toast.makeText(getContext(), "La categoría no contiene productos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        generarOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertLoader alert = new AlertLoader(getActivity(), view);
                cocina = new ArrayList<>();
                barra = new ArrayList<>();
                comal = new ArrayList<>();
                cargarProductos();
                if (comanda != null) {
                    if (listaProductosOrdenados.size() == 0) {
                        Toast.makeText(getContext(), "No se puede ingresar una comanda vacía", Toast.LENGTH_SHORT).show();
                        comanda.getDocumentReference().update("estado", "en espera").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    alert.dimiss("Comanda no modificada y actualizada en estado en espera");
                                } else {
                                    alert.showError(task.getException().toString());
                                }
                            }
                        });
                    } else {
                        if (comanda.getArea().equals("cocina")) {
                            if (cocina.size() == 0) {
                                comanda.getDocumentReference().delete();
                                if (barra.size() > 0) {
                                    firebaseFirestore.collection("Comandas").add(crearComanda("barra", barra)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                notificarBarra("Nueva comanda", mesero + ": Ingresó una comanda");
                                                if (comal.size() > 0) {
                                                    firebaseFirestore.collection("Comandas").add(crearComanda("comal", comal)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            if (task.isSuccessful()) {
                                                                notificarComal("Nueva comanda", mesero + ": Ingresó una comanda");
                                                                alert.dimiss("Orden generada");
                                                            } else {
                                                                alert.showError(task.getException().toString());
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    alert.dimiss("Orden generada");
                                                }
                                            } else {
                                                alert.showError(task.getException().toString());
                                            }
                                        }
                                    });
                                } else {
                                    firebaseFirestore.collection("Comandas").add(crearComanda("comal", comal)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                notificarComal("Nueva comanda", mesero + ": Ingresó una comanda");
                                                alert.dimiss("Orden generada");
                                            } else {
                                                alert.showError(task.getException().toString());
                                            }
                                        }
                                    });
                                }

                            } else {
                                imprimirComandaCocina();
                                comanda.getDocumentReference().update("productos", cocina);
                                comanda.getDocumentReference().update("estado", "corregida").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            notificarCocina("Comanda corregida", mesero + ": corrigió la comanda");
                                            if (barra.size() > 0) {
                                                firebaseFirestore.collection("Comandas").add(crearComanda("barra", barra)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if (task.isSuccessful()) {
                                                            notificarBarra("Nueva comanda", mesero + ": Ingresó una comanda");
                                                            if (comal.size() > 0) {
                                                                firebaseFirestore.collection("Comandas").add(crearComanda("comal", comal)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                        if (task.isSuccessful()) {
                                                                            notificarComal("Nueva comanda", mesero + ": Ingresó una comanda");
                                                                            alert.dimiss("Orden generada");
                                                                        } else {
                                                                            alert.showError(task.getException().toString());
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                alert.dimiss("Orden generada");
                                                            }
                                                        } else {
                                                            alert.showError(task.getException().toString());
                                                        }
                                                    }
                                                });
                                            } else if (comal.size() > 0) {
                                                firebaseFirestore.collection("Comandas").add(crearComanda("comal", comal)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if (task.isSuccessful()) {
                                                            notificarComal("Nueva comanda", mesero + ": Ingresó una comanda");
                                                            alert.dimiss("Orden generada");
                                                        } else {
                                                            alert.showError(task.getException().toString());
                                                        }
                                                    }
                                                });
                                            } else {
                                                alert.dimiss("Comanda corregida");
                                            }
                                        } else {
                                            alert.showError(task.getException().toString());
                                        }
                                    }
                                });
                            }
                        } else if (comanda.getArea().equals("barra")) {
                            if (barra.size() == 0) {
                                comanda.getDocumentReference().delete();
                                if (cocina.size() > 0) {
                                    firebaseFirestore.collection("Comandas").add(crearComanda("cocina", cocina)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                notificarCocina("Nueva comanda", mesero + ": Ingresó una comanda");
                                                if (comal.size() > 0) {
                                                    firebaseFirestore.collection("Comandas").add(crearComanda("comal", comal)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            if (task.isSuccessful()) {
                                                                notificarComal("Nueva comanda", mesero + ": Ingresó una comanda");
                                                                alert.dimiss("Orden generada");
                                                            } else {
                                                                alert.showError(task.getException().toString());
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    alert.dimiss("Orden generada");
                                                }
                                            } else {
                                                alert.showError("Ocurrio un error al ingresar la orden");
                                            }
                                        }
                                    });
                                } else {
                                    firebaseFirestore.collection("Comandas").add(crearComanda("comal", comal)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                notificarComal("Nueva comanda", mesero + ": Ingresó una comanda");
                                                alert.dimiss("Orden generada");
                                            } else {
                                                alert.showError(task.getException().toString());
                                            }
                                        }
                                    });
                                }

                            } else {
                                comanda.getDocumentReference().update("productos", barra);
                                comanda.getDocumentReference().update("estado", "corregida").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            notificarBarra("Comanda corregida", mesero + ": corrigió la comanda");
                                            if (cocina.size() > 0) {
                                                firebaseFirestore.collection("Comandas").add(crearComanda("cocina", cocina)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if (task.isSuccessful()) {
                                                            notificarCocina("Nueva comanda", mesero + ": Ingresó una comanda");
                                                            if (comal.size() > 0) {
                                                                firebaseFirestore.collection("Comandas").add(crearComanda("comal", comal)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                        if (task.isSuccessful()) {
                                                                            notificarComal("Nueva comanda", mesero + ": Ingresó una comanda");
                                                                            alert.dimiss("Orden generada");
                                                                        } else {
                                                                            alert.showError(task.getException().toString());
                                                                        }
                                                                    }
                                                                });
                                                            } else alert.dimiss("Orden generada");
                                                        } else {
                                                            alert.showError("Ocurrio un error al ingresar la orden");
                                                        }
                                                    }
                                                });
                                            } else if (comal.size() > 0) {
                                                firebaseFirestore.collection("Comandas").add(crearComanda("comal", comal)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if (task.isSuccessful()) {
                                                            notificarComal("Nueva comanda", mesero + ": Ingresó una comanda");
                                                            alert.dimiss("Orden generada");
                                                        } else {
                                                            alert.showError(task.getException().toString());
                                                        }
                                                    }
                                                });
                                            } else alert.dimiss("Orden generada");
                                        } else {
                                            alert.showError(task.getException().toString());
                                        }
                                    }
                                });
                            }
                        } else {
                            if (comal.size() == 0) {
                                comanda.getDocumentReference().delete();
                                if (cocina.size() > 0) {
                                    firebaseFirestore.collection("Comandas").add(crearComanda("cocina", cocina)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                notificarCocina("Nueva comanda", mesero + ": Ingresó una comanda");
                                                if (barra.size() > 0) {
                                                    firebaseFirestore.collection("Comandas").add(crearComanda("barra", barra)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            if (task.isSuccessful()) {
                                                                notificarBarra("Nueva comanda", mesero + ": Ingresó una comanda");
                                                                alert.dimiss("Orden generada");
                                                            } else {
                                                                alert.showError(task.getException().toString());
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    alert.dimiss("Orden generada");
                                                }
                                            } else {
                                                alert.showError("Ocurrio un error al ingresar la orden");
                                            }
                                        }
                                    });
                                } else {
                                    firebaseFirestore.collection("Comandas").add(crearComanda("barra", barra)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                notificarBarra("Nueva comanda", mesero + ": Ingresó una comanda");
                                                alert.dimiss("Orden generada");
                                            } else {
                                                alert.showError(task.getException().toString());
                                            }
                                        }
                                    });
                                }

                            } else {
                                comanda.getDocumentReference().update("productos", comal);
                                comanda.getDocumentReference().update("estado", "corregida").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            notificarComal("Comanda corregida", mesero + ": corrigió la comanda");
                                            if (cocina.size() > 0) {
                                                firebaseFirestore.collection("Comandas").add(crearComanda("cocina", cocina)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if (task.isSuccessful()) {
                                                            notificarCocina("Nueva comanda", mesero + ": Ingresó una comanda");
                                                            if (barra.size() > 0) {
                                                                firebaseFirestore.collection("Comandas").add(crearComanda("barra", barra)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                        if (task.isSuccessful()) {
                                                                            notificarBarra("Nueva comanda", mesero + ": Ingresó una comanda");
                                                                            alert.dimiss("Orden generada");
                                                                        } else {
                                                                            alert.showError(task.getException().toString());
                                                                        }
                                                                    }
                                                                });
                                                            } else alert.dimiss("Orden generada");
                                                        } else {
                                                            alert.showError("Ocurrio un error al ingresar la orden");
                                                        }
                                                    }
                                                });
                                            } else if (barra.size() > 0) {
                                                firebaseFirestore.collection("Comandas").add(crearComanda("barra", barra)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if (task.isSuccessful()) {
                                                            notificarBarra("Nueva comanda", mesero + ": Ingresó una comanda");
                                                            alert.dimiss("Orden generada");
                                                        } else {
                                                            alert.showError(task.getException().toString());
                                                        }
                                                    }
                                                });
                                            } else alert.dimiss("Orden generada");

                                        } else {
                                            alert.showError(task.getException().toString());
                                        }
                                    }
                                });
                            }
                        }
                    }
                    comanda = null;
                    totalComanda = 0;
                } else {
                    if (listaProductosOrdenados.size() > 0) {
                        if (cocina.size() > 0) {
                            imprimirComandaCocina();
                            firebaseFirestore.collection("Comandas").add(crearComanda("cocina", cocina)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        notificarCocina("Nueva comanda", mesero + ": Ingresó una comanda");
                                        if (barra.size() > 0) {
                                            firebaseFirestore.collection("Comandas").add(crearComanda("barra", barra)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        notificarBarra("Nueva comanda", mesero + ": Ingresó una comanda");
                                                        if (comal.size() > 0) {
                                                            firebaseFirestore.collection("Comandas").add(crearComanda("comal", comal)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                    if (task.isSuccessful()) {
                                                                        notificarComal("Nueva comanda", mesero + ": Ingresó una comanda");
                                                                        alert.dimiss("Orden generada");
                                                                    } else {
                                                                        alert.showError(task.getException().toString());
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            alert.dimiss("Orden generada");
                                                        }
                                                    } else {
                                                        alert.showError("Ocurrio un error al ingresar la orden");
                                                    }
                                                }
                                            });

                                        } else if (comal.size() > 0) {
                                            firebaseFirestore.collection("Comandas").add(crearComanda("comal", comal)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        notificarComal("Nueva comanda", mesero + ": Ingresó una comanda");
                                                        alert.dimiss("Orden generada");
                                                    } else {
                                                        alert.showError(task.getException().toString());
                                                    }
                                                }
                                            });
                                        } else {
                                            alert.dimiss("Orden generada");
                                        }
                                    } else {
                                        alert.showError(task.getException().toString());
                                    }
                                }
                            });

                        } else if (barra.size() > 0) {
                            firebaseFirestore.collection("Comandas").add(crearComanda("barra", barra)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        notificarBarra("Nueva comanda", mesero + ": Ingresó una comanda");
                                        if (comal.size() > 0) {
                                            firebaseFirestore.collection("Comandas").add(crearComanda("comal", comal)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        notificarComal("Nueva comanda", mesero + ": Ingresó una comanda");
                                                        alert.dimiss("Orden generada");
                                                    } else {
                                                        alert.showError(task.getException().toString());
                                                    }
                                                }
                                            });
                                        } else {
                                            alert.dimiss("Orden generada");
                                        }
                                    } else {
                                        alert.showError("Ocurrio un error al ingresar la orden");
                                    }
                                }
                            });
                        } else {
                            firebaseFirestore.collection("Comandas").add(crearComanda("comal", comal)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        notificarComal("Nueva comanda", mesero + ": Ingresó una comanda");
                                        alert.dimiss("Orden generada");
                                    } else {
                                        alert.showError(task.getException().toString());
                                    }
                                }
                            });
                        }
                    } else {
                        alert.showError("No se ordenaron productos");
                        Navigation.findNavController(view).popBackStack();
                    }
                }

            }
        });

        ((ImageButton) view.findViewById(R.id.btnShowPlatillo)).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View v) {
                if (productos.getCount() == 0 || categorias.getSelectedItemPosition() == categorias.getCount() - 1) {
                    Toast.makeText(getContext(), "No se ha seleccionado ningún producto", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.card_detalle_platillo, null);
                    Platillo aux = adaptadorCategorias.getCategoria(categorias.getSelectedItemPosition()).getPlatillo(productos.getSelectedItemPosition());
                    ((ImageView) dialogView.findViewById(R.id.imgPlatilloDetalle)).setImageURI(aux.getUri());
                    ((TextView) dialogView.findViewById(R.id.platilloDetalle)).setText(aux.getNombre());
                    ((TextView) dialogView.findViewById(R.id.descripcionPlatilloDetalle)).setText(aux.getDescripcion());

                    // Crea el AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setView(dialogView)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Acciones a realizar cuando se hace clic en Aceptar
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        ((Button) view.findViewById(R.id.btnRealizarModificacion)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertLoader alertLoader1 = new AlertLoader(getActivity(), view);
                if (MesasFragment.adapterMesas.getMesa(mesa).getCliente(cliente) != null) {
                    comanda.getDocumentReference().update("productos", listaProductosOrdenados).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                comanda = null;
                                alertLoader1.dimiss("Modificación realizada");
                            } else {
                                alertLoader1.showError("Error al realizar la modificación");
                            }
                        }
                    });
                } else {
                    alertLoader1.dimiss("El cliente no se encuentra registrado");
                }
            }
        });

        addPlatillosCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertLoader alertLoader1 = new AlertLoader(getActivity(), view);
                if (MesasFragment.adapterMesas.getMesa(mesa).getCliente(cliente) != null) {
                    Map comanda = new HashMap();
                    comanda.put("cliente", cliente);
                    comanda.put("mesa", mesa);
                    comanda.put("mesero", mesero);
                    comanda.put("productos", listaProductosOrdenados);
                    comanda.put("area", "correciones");
                    comanda.put("estado", "entregado");
                    comanda.put("fecha", new Date());
                    comanda.put("mensaje", "mensaje");
                    firebaseFirestore.collection("Comandas").add(comanda).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                alertLoader1.dimiss("Productos añadidos a la cuenta");
                            } else {
                                alertLoader1.showError(task.getException().toString());
                            }
                        }
                    });

                } else {
                    alertLoader1.dimiss("El cliente no se encuentra registrado");
                }
            }
        });
        return view;
    }

    @SuppressLint("MissingInflatedId")
    public void agregarProducto(ProductoOrdenado productoOrdenado) {
        listaProductosOrdenados.add(productoOrdenado);
        View cardProductoOrdenado = getLayoutInflater().inflate(R.layout.card_producto_ordenado, null);
        TextView tvCantidadProducto = cardProductoOrdenado.findViewById(R.id.tvCantProduct);
        tvCantidadProducto.setText("" + productoOrdenado.getCantidad());
        ((TextView) cardProductoOrdenado.findViewById(R.id.tvProductOrden)).setText(productoOrdenado.getProducto());
        TextView descrip = cardProductoOrdenado.findViewById(R.id.tvDescripProduct);
        //Toast.makeText(getContext(),productoOrdenado.getDescripcion(),Toast.LENGTH_SHORT).show();
        if (!productoOrdenado.getDescripcion().equals("")) {
            descrip.setText(productoOrdenado.getDescripcion());
            descrip.setVisibility(View.VISIBLE);
        }
        linearLayoutProductosOrdenados.addView(cardProductoOrdenado);
        ((ImageButton) cardProductoOrdenado.findViewById(R.id.btnMenosProducto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(tvCantidadProducto.getText().toString()) != 1) {
                    productoOrdenado.setCantidad(productoOrdenado.getCantidad() - 1);
                    tvCantidadProducto.setText("" + productoOrdenado.getCantidad());
                } else {
                    Toast.makeText(getContext(), "Ya no se puede disminuir, en caso contrario eliminie", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ((ImageButton) cardProductoOrdenado.findViewById(R.id.btnMasProducto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productoOrdenado.setCantidad(productoOrdenado.getCantidad() + 1);
                tvCantidadProducto.setText("" + productoOrdenado.getCantidad());
            }
        });

        ((ImageButton) cardProductoOrdenado.findViewById(R.id.btnRemoveProducto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("¿Está seguro de eliminar el producto de la orden?").
                        setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                linearLayoutProductosOrdenados.removeView(cardProductoOrdenado);
                                listaProductosOrdenados.remove(productoOrdenado);
                                Toast.makeText(getContext(), "Producto eliminado", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog titulo = alert.create();
                titulo.setTitle("Eliminar producto");
                titulo.show();
            }
        });

        cardProductoOrdenado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(productoOrdenado.getArea().equals("comal") && !modificacion && comanda==null && productoOrdenado.getProductos()!=null){
                    Categoria delComal=adaptadorCategorias.getDelComal();
                    if(delComal!=null){
                        final boolean[] primeraSeleccion = {true};
                        View dialogView = getLayoutInflater().inflate(R.layout.card_edit_comal, null);
                        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Spinner selectAntojito=dialogView.findViewById(R.id.spinnerSelecAntojito);
                        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout lyAntojitos=dialogView.findViewById(R.id.lyAntojitos);
                        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText descripAntojitos=dialogView.findViewById(R.id.descripPlatilloPersonalizado);
                        ArrayList<Platillo> platillosComal=new ArrayList<>();
                        platillosComal.add(new Platillo("Seleccione un antojito","",0,true,""));
                        platillosComal.addAll(delComal.getPlatillos());
                        ArrayAdapter<Platillo> adaptadorAnt = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, platillosComal);
                        // Especifica el diseño del menú desplegable
                        adaptadorAnt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        selectAntojito.setAdapter(adaptadorAnt);
                        for(ProductoOrdenado antojito:productoOrdenado.getProductos()){
                            View cardAntojitoOrdenado = getLayoutInflater().inflate(R.layout.card_producto_ordenado, null);
                            TextView tvCantProducto = cardAntojitoOrdenado.findViewById(R.id.tvCantProduct);
                            tvCantProducto.setText(""+antojito.getCantidad());
                            ((TextView) cardAntojitoOrdenado.findViewById(R.id.tvProductOrden)).setText(antojito.getProducto());
                            lyAntojitos.addView(cardAntojitoOrdenado);
                            ((ImageButton)cardAntojitoOrdenado.findViewById(R.id.btnMenosProducto)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(antojito.getCantidad()>1){
                                        antojito.setCantidad(antojito.getCantidad()-1);
                                        tvCantProducto.setText(""+antojito.getCantidad());
                                    }
                                }
                            });

                            ((ImageButton)cardAntojitoOrdenado.findViewById(R.id.btnMasProducto)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    antojito.setCantidad(antojito.getCantidad()+1);
                                    tvCantProducto.setText(""+antojito.getCantidad());
                                }
                            });

                            ((ImageButton)cardAntojitoOrdenado.findViewById(R.id.btnRemoveProducto)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    productoOrdenado.removeProducto(antojito);
                                    lyAntojitos.removeView(cardAntojitoOrdenado);
                                }
                            });
                        }

                        selectAntojito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(primeraSeleccion[0]){
                                    primeraSeleccion[0] =false;
                                }
                                else{
                                    if(position!=0){
                                        Platillo platilloAux=adaptadorAnt.getItem(position);
                                        ProductoOrdenado product=new ProductoOrdenado(platilloAux.getNombre(),platilloAux.getPrecio(),platilloAux.getArea(),1,"");
                                        View cardAntojitoOrdenado = getLayoutInflater().inflate(R.layout.card_producto_ordenado, null);
                                        TextView tvCantidaProducto = cardAntojitoOrdenado.findViewById(R.id.tvCantProduct);
                                        tvCantidaProducto.setText(""+product.getCantidad());
                                        ((TextView) cardAntojitoOrdenado.findViewById(R.id.tvProductOrden)).setText(product.getProducto());
                                        lyAntojitos.addView(cardAntojitoOrdenado);
                                        productoOrdenado.addProducto(product);
                                        ((ImageButton)cardAntojitoOrdenado.findViewById(R.id.btnMenosProducto)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(product.getCantidad()>1){
                                                    product.setCantidad(product.getCantidad()-1);
                                                    tvCantidaProducto.setText(""+product.getCantidad());
                                                }
                                            }
                                        });

                                        ((ImageButton)cardAntojitoOrdenado.findViewById(R.id.btnMasProducto)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                product.setCantidad(product.getCantidad()+1);
                                                tvCantidaProducto.setText(""+product.getCantidad());
                                            }
                                        });

                                        ((ImageButton)cardAntojitoOrdenado.findViewById(R.id.btnRemoveProducto)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                productoOrdenado.removeProducto(product);
                                                lyAntojitos.removeView(cardAntojitoOrdenado);
                                            }
                                        });
                                        selectAntojito.setSelection(0);
                                    }

                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setCancelable(false);

                        builder.setView(dialogView).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(productoOrdenado.getProductos().size()>0){
                                            String descripcion="";
                                            double precioProducto=0;
                                            for(ProductoOrdenado productoOrdenado1:productoOrdenado.getProductos()){
                                                descripcion+=productoOrdenado1.getCantidad()+" "+productoOrdenado1.getProducto()+", ";
                                                precioProducto+=productoOrdenado1.getCantidad()*productoOrdenado1.getPrecio();
                                            }
                                            descripcion=descripcion.substring(0,descripcion.length()-2);
                                            descripcion+=" "+descripAntojitos.getText().toString();
                                            productoOrdenado.setDescripcion(descripcion);
                                            productoOrdenado.setPrecio(precioProducto);
                                            descrip.setText(productoOrdenado.getDescripcion());
                                        }
                                       else{
                                           listaProductosOrdenados.remove(productoOrdenado);
                                           linearLayoutProductosOrdenados.removeView(cardProductoOrdenado);
                                           Toast.makeText(getContext(),"Producto eliminado por inconsistencia",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    else{
                        Toast.makeText(getContext(),"Categoría del comal no encontrada",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    View dialogView = getLayoutInflater().inflate(R.layout.card_edit_producto_ordenado, null);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView producto = dialogView.findViewById(R.id.productEdit);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText iDescripcion = dialogView.findViewById(R.id.etCorreccionProducto);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView iPrecio = dialogView.findViewById(R.id.etCorrecionPrecio);

                    producto.setText(productoOrdenado.getProducto());
                    iDescripcion.setText(productoOrdenado.getDescripcion());
                    iPrecio.setHint("" + productoOrdenado.getPrecio());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false);

                    builder.setView(dialogView).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    productoOrdenado.setDescripcion(iDescripcion.getText().toString());
                                    descrip.setText(productoOrdenado.getDescripcion());
                                    descrip.setVisibility(View.VISIBLE);
                                    if (!iPrecio.getText().toString().equals("")) {
                                        productoOrdenado.setPrecio(Integer.parseInt(iPrecio.getText().toString()));
                                    }
                                }
                            }).
                            setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }
        });
        cant = 1;
        cantidad.setText("" + cant);
        descripcion.setText("");
        descripcion.clearFocus();
    }

    public Map crearComanda(String area, ArrayList<ProductoOrdenado> lista) {
        Map comanda = new HashMap();
        comanda.put("cliente", cliente);
        comanda.put("mesa", mesa);
        comanda.put("mesero", mesero);
        comanda.put("productos", lista);
        comanda.put("area", area);
        comanda.put("estado", "en espera");
        comanda.put("fecha", new Date());
        comanda.put("mensaje", "mensaje");
        return comanda;
    }

    public void notificarCocina(String titulo, String mensaje) {
        firebaseFirestore.collection("Usuarios").whereEqualTo("rol", "Cocinero").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot dc : task.getResult()) {
                        notification.sendMessage(dc.getString("token"), titulo, mensaje);
                    }
                }
            }
        });
    }

    public void notificarComal(String titulo, String mensaje) {
        firebaseFirestore.collection("Usuarios").whereEqualTo("rol", "Comalero").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot dc : task.getResult()) {
                        notification.sendMessage(dc.getString("token"), titulo, mensaje);
                    }
                }
            }
        });
    }

    public void notificarBarra(String titulo, String mensaje) {
        firebaseFirestore.collection("Usuarios").whereEqualTo("rol", "Barman").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot dc : task.getResult()) {
                        notification.sendMessage(dc.getString("token"), titulo, mensaje);
                    }
                }
            }
        });
    }

    public String getNombrePlatilloFormateado(ProductoOrdenado productoOrdenado) {
        if (productoOrdenado.getCantidad() > 1) {
            String nombreFormateado = "";
            String[] palabras = productoOrdenado.getProducto().split(" ");
            int i = 0;
            for (String palabra : palabras) {
                if (i == 1) {
                    if (palabra.length() > 3 && endVocal(palabra)) {
                        palabras[i] = palabra + "s";
                    }
                } else {
                    if (endVocal(palabra)) {
                        palabras[i] = palabra + "s";
                    }
                }
                if (i == 1) {
                    break;
                }
                i++;
            }
            for (String palabra : palabras) {
                nombreFormateado += palabra + " ";
            }
            return nombreFormateado;
        } else {
            return productoOrdenado.getProducto();
        }
    }

    public boolean endVocal(String palabra) {
        return (palabra.charAt(palabra.length() - 1) == 'a' ||
                palabra.charAt(palabra.length() - 1) == 'e' ||
                palabra.charAt(palabra.length() - 1) == 'i' ||
                palabra.charAt(palabra.length() - 1) == 'o' ||
                palabra.charAt(palabra.length() - 1) == 'u' ||
                palabra.charAt(palabra.length() - 1) == 'A' ||
                palabra.charAt(palabra.length() - 1) == 'E' ||
                palabra.charAt(palabra.length() - 1) == 'I' ||
                palabra.charAt(palabra.length() - 1) == 'O' ||
                palabra.charAt(palabra.length() - 1) == 'U');
    }

    public void cargarProductos() {
        for (ProductoOrdenado producto : listaProductosOrdenados) {
            if (producto.getArea().equals("cocina")) {
                cocina.add(producto);
            } else if (producto.getArea().equals("barra")) {
                barra.add(producto);
            } else {
                if (docComal) {
                    comal.add(producto);
                } else {
                    cocina.add(producto);
                }
            }
        }
    }

    public void onDestroy() {
        if (comanda != null && !modificacion) {
            Toast.makeText(getContext(), "Comanda no modificada", Toast.LENGTH_SHORT).show();
            comanda.getDocumentReference().update("estado", "en espera");
        }
        correcion = false;
        modificacion = false;
        super.onDestroy();
    }

    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(descripcion.getWindowToken(), 0);
        }
    }

    public void imprimirComandaCocina() {
        String textoImprimir = "";
        if (comanda != null) {
            textoImprimir += Utils.centrar("Comanda corregida") + "\n";
        }
        textoImprimir += Utils.centrar("Mesa " + mesa + " : " + mesero) + "\n\n";
        for (ProductoOrdenado productoOrdenado : cocina) {
            textoImprimir += formatear(productoOrdenado.getCantidad() + " " + productoOrdenado.getProducto()) + "\n";
            if (!productoOrdenado.getDescripcion().equals("")) {
                textoImprimir +=  formatear(" => " +productoOrdenado.getDescripcion()) + "\n";
            }

        }
        textoImprimir += "\n\n\n";
        System.out.println(textoImprimir);
        ImprimirComanda miAsyncTask = new ImprimirComanda(this, textoImprimir);
        miAsyncTask.execute();
    }

    public String formatear(String string){
        String aux="";
        String linea="";
        String [] palabras=string.split(" ");
        for(String palabra:palabras){
            if(linea.length()+1+palabra.length()>24){
                aux+=linea+"\n";
                linea="  ";
                linea+=palabra+" ";
            }
            else{
                linea+=palabra+" ";
            }
        }
        aux+=linea;

        return aux;
    }

    private void buscarCoincidencias(String filtro) {
        ArrayList<Platillo> coincidencias = new ArrayList<>();
        for (Platillo item : milista) {
            if (item.getNombre().toLowerCase().contains(filtro.toLowerCase())) {
                coincidencias.add(item);
            }
        }
        adaptadorBusqueda.clear();
        adaptadorBusqueda.addAll(coincidencias);
        adaptadorBusqueda.notifyDataSetChanged();
    }


    @Override
    public void onTaskCompleted(String result) {

    }
}