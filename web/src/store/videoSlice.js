const defaultState = {
    isPlayerOpened: false,
    videoId: ""
};

export default function videoReducer(state = defaultState, action){
    switch (action.type) {
        case "OPEN_PLAYER":
            return {...state, isPlayerOpened: true}
        case "CLOSE_PLAYER":
            return {...state, isPlayerOpened: false}
        case "SET_VIDEO_ID":
            return {...state, videoId: action.payload}
        // case "GET_VIDEO_ID":
        //     return {...state, videoId: videoId}
    }
}