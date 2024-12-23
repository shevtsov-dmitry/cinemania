import { create } from "zustand";
import formAddFilmSlice from "./formAddFilmSlice";

const useStore = create((set, get) => ({
  ...formAddFilmSlice(set, get),
}));

export default useStore;
