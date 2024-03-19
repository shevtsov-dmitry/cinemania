import videoReducer from "./videoReducer";


const rootReducer = combineReducers({
    video: videoReducer,
})

export const store = createStore(rootReducer)