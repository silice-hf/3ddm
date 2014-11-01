

package freeh.kitsune.audios;

import freeh.kitsune.Game;
import un.api.CObject;
import un.api.array.Arrays;
import un.api.character.Chars;
import un.api.media.AudioSamples;
import un.api.media.AudioStreamMeta;
import un.api.media.MediaReader;
import un.api.media.MediaStore;
import un.api.media.Medias;
import un.api.io.ArrayOutputStream;
import un.api.io.DataOutputStream;
import un.api.io.IOException;
import un.api.io.NumberEncoding;
import un.api.path.Path;

/**
 *
 */
public class Music extends CObject{
    
    private final Path path;
    private AudioStreamMeta meta;
    
    public Music(Path path) {
        this.path = path;
    }
    
    public Chars getName(){
        return new Chars(path.getName());
    }

    public AudioStreamMeta getAudioStreamMeta() {
        return meta;
    }
    
    public byte[] createPCMBuffer(){
        try{
            final MediaStore store = Medias.open(path);

            //tranform audio in a supported byte buffer
            meta = (AudioStreamMeta) store.getStreamsMeta()[0];
            final MediaReader reader = store.createReader(null);

            //recode stream in a stereo 16 bits per sample.
            final ArrayOutputStream out = new ArrayOutputStream();
            final DataOutputStream ds = new DataOutputStream(out, NumberEncoding.LITTLE_ENDIAN);

            while(reader.hasNext()){
                reader.next();
                final AudioSamples samples = (AudioSamples) reader.getBuffers()[0];
                final int[] audiosamples = samples.asPCM(null, 16);
                if(audiosamples.length==1){
                    ds.writeUShort(audiosamples[0]);
                    ds.writeUShort(audiosamples[0]);
                }else{
                    ds.writeUShort(audiosamples[0]);
                    ds.writeUShort(audiosamples[1]);
                }
            }

            return out.getBuffer().toArray();

        }catch(IOException ex){
            Game.LOGGER.warning(ex);
            return Arrays.ARRAY_BYTE_EMPTY;
        }
    }
    
    public Chars toChars() {
        return getName();
    }
}
