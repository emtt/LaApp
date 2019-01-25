package laapp.emt.com.core.rest;

import io.reactivex.Observable;
import laapp.emt.com.core.Constants;
import laapp.emt.com.core.model.Contacto;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiInterface {
    /**
     * Returns the number of the user if its registered in backend.
     *
     * @param url an absolute URL giving the number of the device
     * @return the object "Contacto" for the response.
     */

    /*
    @Headers({ACCEPT_JSON_HEADER})
    @POST
    Observable<Contacto> searchContacto(@Url String url, @Field("telefono") String telefono);
    */
    @FormUrlEncoded
    @POST(Constants.SEARCH_TELEFONO)
    Observable<Contacto> searchContacto(@Field("telefono") String telefono);
    //Observable<Contacto> searchContacto(@Body Contacto contacto);
    //Observable<Contacto> searchContacto(@Field("telefono") String telefono);

}
