export function VideoPlayer() {
    return <div className="w-fit flex justify-center flex-col">
        <h1 className="font-bold font-mono text-center">VIDEO PLAYER</h1>
        <video width="320" height="240" controls>
            <source src="http://localhost:8081/videos/stream/65e5f9010986dd76a8edb246" type="video/mp4"/>
            Your browser does not support the video tag.
        </video>
    </div>
}