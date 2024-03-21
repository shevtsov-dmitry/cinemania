import { createSlice } from "@reduxjs/toolkit";

export const videoPlayerSlice = createSlice({
    name: "video",
    initialState: {
        isPlayerOpened: false,
        videoId: "",
    },
    reducers: {
        setPlayerOpened: (state, action) => {
            state.isPlayerOpened = action.payload;
        },
        setVideoId: (state, action) => {
            state.videoId = action.payload;
        }
    }
})

export const { setPlayerOpened, setVideoId } = videoPlayerSlice.actions
export default videoPlayerSlice.reducer
