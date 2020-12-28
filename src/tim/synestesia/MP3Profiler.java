package tim.synestesia;

import javazoom.jl.decoder.*;

import java.io.*;

public class MP3Profiler {
    private ByteArrayOutputStream decodedOutputStream;
    private String pathToMP3File;
    private float MP3DurationInMS;
    private final float MAX_PROFILING_DURATION_IN_MS = 10000;

    public float getMP3DurationInMS() {
        return this.MP3DurationInMS;
    }


    public MP3Profiler(String pathToMP3File) {
        this.pathToMP3File = pathToMP3File;
        this.MP3DurationInMS = 0;
        this.decodedOutputStream = new ByteArrayOutputStream(1024);
    }




    public byte[] getDecodedPulseModulationArray() throws BitstreamException, IOException, DecoderException {
        decodeMP3File();
        return decodedOutputStream.toByteArray();
    }


    public void decodeMP3File() throws IOException, BitstreamException, DecoderException, IllegalArgumentException {
        boolean isProfilingTerminated = false;

        SampleBuffer decodedFrameOutput = null;

        Bitstream decodingStream = new Bitstream(new FileInputStream(this.pathToMP3File));

        while (!isProfilingTerminated) {
            Header currentFrameHeader = decodingStream.readFrame();
            if (currentFrameHeader == null) {
                isProfilingTerminated = true;
            } else {
                float currentFrameDuration = currentFrameHeader.ms_per_frame();
                if(this.MP3DurationInMS + currentFrameDuration >= MAX_PROFILING_DURATION_IN_MS) {
                    decodingStream.closeFrame();
                    break;
                }
                this.MP3DurationInMS += currentFrameDuration;
                Decoder MP3Decoder = new Decoder();
                decodedFrameOutput = (SampleBuffer) MP3Decoder.decodeFrame(currentFrameHeader, decodingStream);
                //AddPulseModulationToDecodedOutputStream();
                short[] decodedPulseModulation = decodedFrameOutput.getBuffer();
                //System.out.println(decodedPulseModulation);
                for (short pulse : decodedPulseModulation) {
                    this.decodedOutputStream.write(getFirstChannel(pulse));
                    this.decodedOutputStream.write(getSecondChannel(pulse));
                }
            }


                decodingStream.closeFrame();
            }
        }


    private byte getFirstChannel(short pulse) {
        return (byte) (pulse & 0xff);
    }
    private byte getSecondChannel(short pulse) {
        return (byte) ((pulse >> 8) & 0xff);
    }
}