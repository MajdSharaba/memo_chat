let localVideo = document.getElementById("local-video")
let remoteVideo = document.getElementById("remote-video")

localVideo.style.opacity = 0
remoteVideo.style.opacity = 0

localVideo.onplaying = () => { localVideo.style.opacity = 1 }
remoteVideo.onplaying = () => { remoteVideo.style.opacity = 1 }

let peer
let runningCall;
let shouldFaceUser = true;
function init(b) {
    peer = new Peer(undefined, {
        host: 'peerfadi.herokuapp.com',
        port: 443,
        secure:true
    })

      peer.on('open', (peerId) => {
           Android.onPeerConnected(peerId)
       })

    listen(b)
}

let localStream
function listen(b) {
    peer.on('call', (call) => {

        navigator.getUserMedia({
            audio: true,
            video: true
        }, (stream) => {
            localVideo.srcObject = stream
            localStream = stream
             if (b == "true") {
                    localStream.getVideoTracks()[0].enabled = true
                } else {
                    localStream.getVideoTracks()[0].enabled = false
                }

            runningCall = call
            runningCall.answer(stream)
            runningCall.on('stream', (remoteStream) => {
//            var newRemoteStream=new MediaStream(remoteStream.getAudioTracks())
                remoteVideo.srcObject = remoteStream

                remoteVideo.className = "primary-video"
                localVideo.className = "secondary-video"

            })

        })

    })
}

function startCall(otherUserId,b) {
    navigator.getUserMedia({
        audio: true,
        video: true
    }, (stream) => {

        localVideo.srcObject = stream
//        localStream.getVideoTracks()[0].enabled = false
        localStream = stream
  if (b == "true") {
        localStream.getVideoTracks()[0].enabled = true
    } else {
        localStream.getVideoTracks()[0].enabled = false
    }

        runningCall = peer.call(otherUserId, stream)
        runningCall.on('stream', (remoteStream) => {
//                    var newRemoteStream=new MediaStream(localStream.getAudioTracks())

            remoteVideo.srcObject = remoteStream



            remoteVideo.className = "primary-video"
            localVideo.className = "secondary-video"
        })

    })
}

function toggleVideo(b) {
    if (b == "true") {
        localStream.getVideoTracks()[0].enabled = true
    } else {
        localStream.getVideoTracks()[0].enabled = false
    }
}

function toggleAudio(b) {
    if (b == "true") {
        localStream.getAudioTracks()[0].enabled = true
    } else {
        localStream.getAudioTracks()[0].enabled = false
    }
}
async function toggleCamera() {

   // const currentTrack = localStream.getVideoTracks()
    let senders = runningCall.peerConnection.getSenders();
    shouldFaceUser = !shouldFaceUser
    navigator.getUserMedia({
    audio: true,
    video: {
      facingMode: shouldFaceUser ? 'user' : 'environment'
    }
  }, (stream) => {
//    Android.print()
//   Android.messageHandlers.bridge.postMessage("senders[0] = "+ JSON.stringify(senders[0]))

      localVideo.srcObject = stream
      localStream = stream
      localVideo.playsInline = true;
      let video_track = stream.getVideoTracks()[0];
//      let video_track = stream.getVideoTracks();

      senders[1].replaceTrack(video_track);
//            senders.replaceTrack(video_track);

//      localStream.removeTrack(currentTrack[0])
//      localStream.addTrack(stream.getVideoTracks()[0])
    // peer.replaceTrack(localStream.getVideoTracks()[0])
    })

}