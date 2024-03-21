import videoPlayerSlice from "./videoPlayerSlice";
import { configureStore } from "@reduxjs/toolkit";

export const store = configureStore({
    reducer: {
        videoPlayer: videoPlayerSlice
    }
})
