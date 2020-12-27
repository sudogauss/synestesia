package tim.synestesia;


import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.DecoderException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        MP3Profiler test = new MP3Profiler("/home/tsimafei/Downloads/test.mp3");
        try {
            byte[] testArray = test.getDecodedPulseModulationArray();
            for(byte a : testArray) {
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
