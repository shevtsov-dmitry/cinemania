const URL = process.env.REACT_APP_URL
let videoName = "clip"
let uri = URL + "videos/stream/" + videoName
export function VideoPlayer(){
    return <>
        <video controls autoplay className={"w-dvw h-dvh"} muted>
            <source src={uri} type="video/mp4"/>
        </video>
    </>
}