package com.example.myapplication2;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class SuperSaveZone {

    private Globals globals;
    private StatsService Service;
    long st_time;
    long st_i;
    long delta_time;
    public static final long CHECK_TIME_IN = 1529571589;
    public static final long CHECK_TIME_OUT = 1529571589;
    double circle_radius;
    String save_zones;
    LatLng[] stalkers_save_zones_in = new LatLng[95];
    LatLng[] military_save_zones_in = new LatLng[12];
    LatLng[] red_save_zones_out = new LatLng[24];
    LatLng[] green_save_zones_out = new LatLng[24];
    private static final String TAG = "SuperSave";




    public SuperSaveZone( long start_time, long start_i, long delta, double radius, String zones){
        st_time = start_time;
        st_i = start_i;
        delta_time = delta;
        circle_radius = radius;
        save_zones = zones;
        create_stalkers_zones_in();
        create_red_zones_out();
        create_military_zones_in();
        create_green_zones_out();
    }

    public void create_stalkers_zones_in(){
        LatLng[] latLngs = new LatLng[95];
        latLngs[0] = new LatLng(64.354129d, 40.743913d);// кпп и далее к выходу
        latLngs[1] = new LatLng(64.354235d, 40.744258d);
        latLngs[2] = new LatLng(64.35432521220459d, 40.74460558362928d);
        latLngs[3] = new LatLng(64.35445293046173d, 40.744923831508146d);
        latLngs[4] = new LatLng(64.3545824026996d, 40.745214162825555d);
        latLngs[5] = new LatLng(64.35472209740922d, 40.74546291301759d);
        latLngs[6] = new LatLng(64.35489346502892d, 40.745582845300504d);
        latLngs[7] = new LatLng(64.35500782797686d, 40.74526722512475d);
        latLngs[8] = new LatLng(64.35507067122896d, 40.74488372241825d);
        latLngs[9] = new LatLng(64.35513902594106d, 40.74451641974487d);
        latLngs[10] = new LatLng(64.3552087889606d, 40.74412309924951d);
        latLngs[11] = new LatLng(64.3552827954611d, 40.743742309081476d);
        latLngs[12] = new LatLng(64.35534417931994d, 40.74335453065853d);
        latLngs[13] = new LatLng(64.35541176405508d, 40.74296775537176d);
        latLngs[14] = new LatLng(64.35555348581504d, 40.742687429518696d);
        latLngs[15] = new LatLng(64.35563584135113d, 40.742319967224745d);
        latLngs[16] = new LatLng(64.3557006595403d, 40.74194897608368d);
        latLngs[17] = new LatLng(64.3558133075042d, 40.74161370149263d);
        latLngs[18] = new LatLng(64.35595448110925d, 40.74133608438898d);
        latLngs[19] = new LatLng(64.35609017983464d, 40.74107015979084d);
        latLngs[20] = new LatLng(64.35624275507247d, 40.74082160943004d);

        latLngs[21] = new LatLng(64.35295356108362d, 40.7434670952109d); //толчек у монолита
        latLngs[22] = new LatLng(64.35280265443103d, 40.74370276887028d);
        latLngs[23] = new LatLng(64.3526529104659d, 40.74394533079032d);
        latLngs[24] = new LatLng(64.35247292472532d, 40.74397037100172d);
        latLngs[25] = new LatLng(64.35234879448167d, 40.74365386149042d);
        latLngs[26] = new LatLng(64.35225051500136d, 40.7433202479478d);
        latLngs[27] = new LatLng(64.3521427267776d, 40.74298846111945d);
        latLngs[28] = new LatLng(64.35208781625262d, 40.74256105302851d);
        latLngs[29] = new LatLng(64.35206614369244d, 40.742176031451784d);
        latLngs[30] = new LatLng(64.35203133624847d, 40.74175692849512d);
        latLngs[31] = new LatLng(64.3519613993229d, 40.741380966909865d);

        latLngs[32] = new LatLng(64.35276759841277d, 40.74260360547909d); // путь военных
        latLngs[33] = new LatLng(64.35269821146606d, 40.742199371531434d);
        latLngs[34] = new LatLng(64.35259972068465d, 40.741861999009714d);
        latLngs[35] = new LatLng(64.35251103312895d, 40.741499425573956d);
        latLngs[36] = new LatLng(64.35247640158082d, 40.74108919460003d);
        latLngs[37] = new LatLng(64.35243869370726d, 40.74068559251896d);
        latLngs[38] = new LatLng(64.35242336435232d, 40.740270397903735d);

        latLngs[39] = new LatLng(64.35317903326629d, 40.74211621986506d); // самый жирный
        latLngs[40] = new LatLng(64.3530676855036d, 40.74177850748372d);
        latLngs[41] = new LatLng(64.35307759043815d, 40.74136361752823d);
        latLngs[42] = new LatLng(64.35311559862588d, 40.740947540217554d);
        latLngs[43] = new LatLng(64.35320833036639d, 40.7406033580693d);
        latLngs[44] = new LatLng(64.3533427514016d, 40.74088981008373d);
        latLngs[45] = new LatLng(64.3534273880053d, 40.74125741173395d);
        latLngs[46] = new LatLng(64.3535272506385d, 40.7416104341624d);
        latLngs[47] = new LatLng(64.35369931415698d, 40.74167856221654d);
        latLngs[48] = new LatLng(64.35385106807306d, 40.741420371104475d);
        latLngs[49] = new LatLng(64.35399244102797d, 40.74117024644733d);
        latLngs[50] = new LatLng(64.35399947701931d, 40.740757545914946d);
        latLngs[51] = new LatLng(64.35413600828822d, 40.74047520189392d);
        latLngs[52] = new LatLng(64.35431770759529d, 40.74047530249902d);
        latLngs[53] = new LatLng(64.35448818768883d, 40.74060662377004d);
        latLngs[54] = new LatLng(64.3546205550841d, 40.740366451897d);
        latLngs[55] = new LatLng(64.35463945956784d, 40.73993880157785d);
        latLngs[56] = new LatLng(64.35465628840566d, 40.739523336212606d);
        latLngs[57] = new LatLng(64.35468482593951d, 40.73910560830513d);
        latLngs[58] = new LatLng(64.35483549689444d, 40.738899002714625d);
        latLngs[59] = new LatLng(64.354993892013d, 40.73867466493646d);
        latLngs[60] = new LatLng(64.35498453249937d, 40.7382720979003d);
        latLngs[61] = new LatLng(64.35492709824331d, 40.7378803923697d);

        latLngs[62] = new LatLng(64.35289419918085d, 40.74006914469832d);//самая короткая
        latLngs[63] = new LatLng(64.35282882522654d, 40.73965389027957d);
        latLngs[64] = new LatLng(64.35281241378898d, 40.739259701118684d);

        latLngs[65] = new LatLng(64.35327464552576d, 40.74278657071493d); // самая важная
        latLngs[66] = new LatLng(64.35338909350193d, 40.74310977309313d);
        latLngs[67] = new LatLng(64.35339042102528d, 40.742703076129935d);
        latLngs[68] = new LatLng(64.35357354817818d, 40.74276856634414d);
        latLngs[69] = new LatLng(64.35374926753758d, 40.74276936833783d);
        latLngs[70] = new LatLng(64.35392842206795d, 40.74275131202157d);
        latLngs[71] = new LatLng(64.35406736165523d, 40.74247493169485d);
        latLngs[72] = new LatLng(64.35424611985239d, 40.7425261775552d);
        latLngs[73] = new LatLng(64.35442159914702d, 40.74253051689229d);
        latLngs[74] = new LatLng(64.35459589159377d, 40.74244715268726d);
        latLngs[75] = new LatLng(64.35473228695206d, 40.7421457820547d);
        latLngs[76] = new LatLng(64.35474349130439d, 40.74172442186285d);
        latLngs[77] = new LatLng(64.35474117057692d, 40.741743502867166);
        latLngs[78] = new LatLng(64.35480112579775d, 40.741346431397666d);
        latLngs[79] = new LatLng(64.35492614666789d, 40.741040636954516d);
        latLngs[80] = new LatLng(64.3550648535183d, 40.740787868746104d);
        latLngs[81] = new LatLng(64.35523643165064d, 40.74092887045961d);
        latLngs[82] = new LatLng(64.35535259212864d, 40.740618210093004d);
        latLngs[83] = new LatLng(64.35538887008047d, 40.740199498863824d);
        latLngs[84] = new LatLng(64.35546413114129d, 40.73981468710406d);
        latLngs[85] = new LatLng(64.35556608591283d, 40.739482952991544d);
        latLngs[86] = new LatLng(64.35566787403617d, 40.739141199432446d);
        latLngs[87] = new LatLng(64.35562164086585d, 40.73873399434004d);
        latLngs[88] = new LatLng(64.35566212114979d, 40.7383278893475d);
        latLngs[89] = new LatLng(64.3558329544728d, 40.738182241545026d);
        latLngs[90] = new LatLng(64.35471398517412d, 40.74072987952912d);//отворотки
        latLngs[91] = new LatLng(64.3540625268518d, 40.74008438658268d);//отворотки
        latLngs[92] = new LatLng(64.35388053787214d, 40.74008035258441d);//отворотки
        latLngs[93] = new LatLng(64.35511363345678d, 40.7435522554439d);//отворотки
        latLngs[94] = new LatLng(64.35499368143708d, 40.74324220074001d);//отворотки
        stalkers_save_zones_in = latLngs;
    }
    public void create_military_zones_in(){
        LatLng[] latLngs = new LatLng[12];
        latLngs[0] = new LatLng(64.35635328466024d, 40.740526559484145d);// ночные
        latLngs[1] = new LatLng(64.35648749695677d, 40.74024632672407d);//
        latLngs[2] = new LatLng(64.35662201786853d, 40.7399589942588d);//
        latLngs[3] = new LatLng(64.3567484782622d, 40.73967596773043d);// ночные
        latLngs[4] = new LatLng(64.35190461794156d, 40.740989107818386d);//ночные
        latLngs[5] = new LatLng(64.35186262065666d, 40.74058758789669d);// ночные
        latLngs[6] = new LatLng(64.3517975167356d, 40.74019942527292d);// ночные
        latLngs[7] = new LatLng(64.3548623948148d, 40.73747803747212d);// ночные
        latLngs[8] = new LatLng(64.35479771816406d, 40.73709830414507d);// ночные
        latLngs[9] = new LatLng(64.35279819399817d, 40.73884715079873d);//ночная
        latLngs[10] = new LatLng(64.35597785931614d, 40.73796221719635d);
        latLngs[11] = new LatLng(64.35606751644602d, 40.73759465692494d);
        /*latLngs[12] = new LatLng(64.531386d, 40.156352d);
        latLngs[13] = new LatLng(64.531305d, 40.155756d);
        latLngs[14] = new LatLng(64.531237d, 40.155161d);
        latLngs[15] = new LatLng(64.531174d, 40.154546d);
        latLngs[16] = new LatLng(64.530960d, 40.154246d);
        latLngs[17] = new LatLng(64.530700d, 40.154412d);
        latLngs[18] = new LatLng(64.530441d, 40.154562d);
        latLngs[19] = new LatLng(64.530173d, 40.154662d);*/
        military_save_zones_in = latLngs;
    }
    public void create_red_zones_out(){
        LatLng[] latLngs = new LatLng[24];
        latLngs[0] = new LatLng(64.529827d, 40.154909d);
        latLngs[1] = new LatLng(64.530166d, 40.154597d);
        latLngs[2] = new LatLng(64.530531d, 40.154565d);
        latLngs[3] = new LatLng(64.530880d, 40.154313d);
        latLngs[4] = new LatLng(64.531219d, 40.154005d);
        latLngs[5] = new LatLng(64.531563d, 40.153774d);
        latLngs[6] = new LatLng(64.531903d, 40.153554d);
        latLngs[7] = new LatLng(64.532108d, 40.152889d);
        latLngs[8] = new LatLng(64.532016d, 40.152133d);
        latLngs[9] = new LatLng(64.531947d, 40.151258d);
        latLngs[10] = new LatLng(64.532062d, 40.150518d);
        latLngs[11] = new LatLng(64.532418d, 40.150261d);
        latLngs[12] = new LatLng(64.532769d, 40.150051d);
        latLngs[13] = new LatLng(64.533023d, 40.150733d);
        latLngs[14] = new LatLng(64.533167d, 40.151465d);
        latLngs[15] = new LatLng(64.533365d, 40.152181d);
        latLngs[16] = new LatLng(64.533732d, 40.152390d);
        latLngs[17] = new LatLng(64.534071d, 40.152133d);
        latLngs[18] = new LatLng(64.534436d, 40.152131d);
        latLngs[19] = new LatLng(64.534792d, 40.151943d);
        latLngs[20] = new LatLng(64.535136d, 40.152013d);
        latLngs[21] = new LatLng(64.535187d, 40.152796d);
        latLngs[22] = new LatLng(64.535265d, 40.153574d);
        latLngs[23] = new LatLng(64.535231d, 40.154502d);
        red_save_zones_out = latLngs;
    }
    public void create_green_zones_out(){
        LatLng[] latLngs = new LatLng[24];
        latLngs[0] = new LatLng(64.529662d, 40.155804d);
        latLngs[1] = new LatLng(64.529597d, 40.156453d);
        latLngs[2] = new LatLng(64.529523d, 40.157064d);
        latLngs[3] = new LatLng(64.529736d, 40.157440d);
        latLngs[4] = new LatLng(64.530001d, 40.157161d);
        latLngs[5] = new LatLng(64.530255d, 40.156962d);
        latLngs[6] = new LatLng(64.530514d, 40.156785d);
        latLngs[7] = new LatLng(64.530787d, 40.156630d);
        latLngs[8] = new LatLng(64.531060d, 40.156667d);
        latLngs[9] = new LatLng(64.531307d, 40.156420d);
        latLngs[10] = new LatLng(64.531558d, 40.156152d);
        latLngs[11] = new LatLng(64.531778d, 40.155863d);
        latLngs[12] = new LatLng(64.532013d, 40.155621d);
        latLngs[13] = new LatLng(64.532248d, 40.155279d);
        latLngs[14] = new LatLng(64.532403d, 40.155000d);
        latLngs[15] = new LatLng(64.532645d, 40.154828d);
        latLngs[16] = new LatLng(64.532914d, 40.154784d);
        latLngs[17] = new LatLng(64.532967d, 40.155337d);
        latLngs[18] = new LatLng(64.533235d, 40.155482d);
        latLngs[19] = new LatLng(64.533515d, 40.155348d);
        latLngs[20] = new LatLng(64.533778d, 40.155187d);
        latLngs[21] = new LatLng(64.534032d, 40.155020d);
        latLngs[22] = new LatLng(64.534211d, 40.154913d);
        latLngs[23] = new LatLng(64.534525d, 40.154822d);
        green_save_zones_out = latLngs;
    }

    public LatLng[] Choose_save_zones(){
        LatLng[] zones;
        switch (save_zones){
            case "stalkers_in":
                zones = stalkers_save_zones_in;
                break;
            case "stalkers_out":
                zones = red_save_zones_out;
                break;
            case "military_in":
                zones = military_save_zones_in;
                break;
            case "green_out":
                zones = green_save_zones_out;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + save_zones);
        }
        return zones;
    }

    public CircleOptions Draw_save_zone(Location locationMy){
        LatLng[] zones = Choose_save_zones();
        CircleOptions circleOptions = null;

        Location location = new Location("");

        long timeInSeconds = (Calendar.getInstance().getTimeInMillis() / 1000);
        for (long i = 0; i < (zones.length /*/ 2*/); i++){
            location.setLatitude(zones[(int) i].latitude);
            location.setLongitude(zones[(int) i].longitude);
            double distanceToCircle = location.distanceTo(locationMy);
            if (distanceToCircle <= circle_radius){
                circleOptions = new CircleOptions().center(zones[(int) (i)]).radius(circle_radius).strokeColor(Color.WHITE).strokeWidth(3).zIndex(Float.MAX_VALUE);
                break;
            }


            /*if ((timeInSeconds >= (st_time + i * delta_time)) && (timeInSeconds <= (st_time + (i + 1) * delta_time))){
                    circleOptions = new CircleOptions().center(zones[(int) (2 * i + st_i)]).radius(circle_radius).strokeColor(Color.WHITE).strokeWidth(3).zIndex(Float.MAX_VALUE);
                    break;
            }*/ else {
                    circleOptions = null;
            }

        }
        return circleOptions;
    }

    private Calendar cal = Calendar.getInstance();
    private int Hour = this.cal.get(10);
    public LatLng Check_super_save_zone(){
        LatLng[] zones = Choose_save_zones();
        int j = -1;
        long timeInSeconds = (Calendar.getInstance().getTimeInMillis() / 1000);
        for (long i = 0; i < (zones.length /*/ 2*/); i++){
            if (zones.equals(military_save_zones_in) && (Hour >= 22 | Hour <= 4)/*(timeInSeconds >= (st_time + i * delta_time)) && (timeInSeconds <= (st_time + (i + 1) * delta_time))*/){
                j = (int) (i);
                break;
            }

        }

        if (j == -1) {
            return new LatLng(0, 0);
        } else{
            return zones[j];
        }
    }



}
