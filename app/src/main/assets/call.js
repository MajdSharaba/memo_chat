let localVideo = document.getElementById("local-video")
let remoteVideo = document.getElementById("remote-video")

localVideo.style.opacity = 0
remoteVideo.style.opacity = 0

localVideo.onplaying = () => { localVideo.style.opacity = 1 }
remoteVideo.onplaying = () => { remoteVideo.style.opacity = 1 }

let peer
function init(b) {
    peer = new Peer(undefined, {
        host: 'mypeerjs3002.herokuapp.com',
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


            call.answer(stream)
            call.on('stream', (remoteStream) => {
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

        const call = peer.call(otherUserId, stream)
        call.on('stream', (remoteStream) => {
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