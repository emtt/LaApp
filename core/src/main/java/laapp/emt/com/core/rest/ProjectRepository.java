package laapp.emt.com.core.rest;

import android.content.Context;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.readystatesoftware.chuck.ChuckInterceptor;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import laapp.emt.com.core.Constants;
import laapp.emt.com.core.model.Contacto;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProjectRepository {

    private static String TAG = ProjectRepository.class.getSimpleName();
    private static ProjectRepository projectRepository;
    private ApiInterface apiInterface;
    private Context context;

    public synchronized static ProjectRepository getInstance(Context mContext) {

        if (projectRepository == null) {
            if (projectRepository == null) {
                projectRepository = new ProjectRepository(mContext);
            }
        }
        return projectRepository;
    }

    private ProjectRepository(Context mContext) {
        this.context = mContext;
        /**
         Comment by Efrain Morales
         We can create an interceptor for check the traffic using HttpLoggingInterceptor
         or ChuckInterceptor which isn't added to this project. So I leave this comment just for info.
         HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
         logging.setLevel(HttpLoggingInterceptor.Level.BODY);
         or
         httpClient.addInterceptor(new ChuckInterceptor(App.getAppContext()));
         **/
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new ChuckInterceptor(context));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);

    }

    /**
     * Search in backend for the number of the device, if it's registered then
     * response an Contacto object trough and RX Observable for the ViewModel.
     *
     * @param telefono The telefono of the device.
     * @return Observable<Contacto>
     */
    public Observable<Contacto> searchContacto(String telefono) {
        return apiInterface.searchContacto(telefono)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Contacto> registerUser(String userid) {
        return apiInterface.registerUser(userid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
