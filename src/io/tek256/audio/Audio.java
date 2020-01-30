package io.tek256.audio;

import java.nio.FloatBuffer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

public class Audio {
	public static FloatBuffer orientation = BufferUtils.createFloatBuffer(6);
	private static float pitch = 1f, volume = 1f;

	public Audio(){
		
	}
	
	public static void setVolume(float volume){
		if(Audio.volume == volume)
			return;
		Audio.volume = volume;
		volume = (volume > 1) ? volume / 100f : volume; //normalize 
		AL10.alListenerf(AL10.AL_GAIN, volume);
	}

	public static void setPitch(float pitch){
		if(Audio.pitch == pitch)
			return;
		Audio.pitch = pitch;
		AL10.alListenerf(AL10.AL_PITCH, pitch);
	}
	
	public static void setListenerf(int param, float value){
		AL10.alListenerf(param, value);
	}
	
	public static void setListeneri(int param, int value){
		AL10.alListeneri(param, value);
	}
	
	public static void setListenerPosition(Vector3f position){
		AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
	}
	
	public static void setListenerRotation(Vector3f rotation){
		orientation.put(new float[]{rotation.x,rotation.y,rotation.z,0f,1f,0f}).rewind();
		AL10.alListenerfv(AL10.AL_ORIENTATION, orientation);
	}
}
