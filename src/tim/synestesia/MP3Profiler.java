package tim.synestesia;

import javazoom.jl.decoder.*;

import java.io.*;

public class MP3Profiler {
    private File MP3file;
    private ByteArrayOutputStream decodedOutputStream;
    private InputStream audioFileInputStream;
    private String pathToMP3File;
    private float MP3DurationInMS;
    private Bitstream MP3FramesToDecode;
    private Decoder MP3Decoder;
    private final float MAX_PROFILING_DURATION_IN_MS = 10000;
    private short[] decodedPulseModulation;
    private SampleBuffer decodedFrameOutput;

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


    private void decodeMP3File() throws IOException, BitstreamException, DecoderException, IllegalArgumentException {
        openMP3FileForDecoding();
        defineProfilingInstruments();
        boolean isProfilingTerminated = false;

        while (!isProfilingTerminated) {
            Header currentFrameHeader = this.MP3FramesToDecode.readFrame();
            if (currentFrameHeader == null) {
                isProfilingTerminated = true;
                continue;
            }
            float currentFrameDuration = currentFrameHeader.ms_per_frame();
            if (this.MP3DurationInMS + currentFrameDuration >= MAX_PROFILING_DURATION_IN_MS) {
                isProfilingTerminated = true;
                continue;
            }
            this.MP3DurationInMS += currentFrameDuration;
            this.decodedFrameOutput = (SampleBuffer) this.MP3Decoder.decodeFrame(currentFrameHeader, this.MP3FramesToDecode);
            if (this.decodedFrameOutput.getSampleFrequency() != 44100
                    || this.decodedFrameOutput.getChannelCount() != 2) {
                throw new IllegalArgumentException("Mono or Non-44100 MP3 is not supported");
            }
            AddPulseModulationToDecodedOutputStream();

        }
        this.MP3FramesToDecode.closeFrame();
    }


    private void openMP3FileForDecoding() throws IOException {
        this.MP3file = new File(this.pathToMP3File);
        this.audioFileInputStream = new BufferedInputStream(new FileInputStream(this.MP3file));
    }

    private void defineProfilingInstruments() throws IOException {
        this.MP3FramesToDecode = new Bitstream(this.audioFileInputStream);
        this.MP3Decoder = new Decoder();
    }

    private void AddPulseModulationToDecodedOutputStream() {
        this.decodedPulseModulation = this.decodedFrameOutput.getBuffer();
        for(short pulse : this.decodedPulseModulation) {
            this.decodedOutputStream.write(getFirstChannel(pulse));
            this.decodedOutputStream.write(getSecondChannel(pulse));
        }
    }

    private byte getFirstChannel(short pulse) {
        return (byte) (pulse & 0xff);
    }
    private byte getSecondChannel(short pulse) {
        return (byte) ((pulse >> 8) & 0xff);
    }
}