// Get the audio element
var audio = document.getElementById("midiPlayer");

// Play the MIDI file
function playMidi() {
  audio.play();
}

// Pause the MIDI file
function pauseMidi() {
  audio.pause();
}

// Stop the MIDI file
function stopMidi() {
  audio.pause();
  audio.currentTime = 0;
}