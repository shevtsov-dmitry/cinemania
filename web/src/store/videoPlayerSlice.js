import { createSlice } from '@reduxjs/toolkit'

export const videoPlayerSlice = createSlice({
    name: 'video',
    initialState: {
        videoId: '',
    },
    reducers: {
        setVideoId: (state, action) => {
            state.videoId = action.payload
        },
    },
})

export const { setVideoId } = videoPlayerSlice.actions
export default videoPlayerSlice.reducer
