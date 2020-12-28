package tim.synestesia;


import javazoom.jl.decoder.*;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws DecoderException, BitstreamException, IOException {


        MP3Profiler test = new MP3Profiler("/home/tsimafei/Downloads/test.mp3");
        try {
            byte[] test1 = test.getDecodedPulseModulationArray();
            for( byte a : test1) {
                System.out.println(a);
            }
        } catch (BitstreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DecoderException e) {
            e.printStackTrace();
        }
    }
}



