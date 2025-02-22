import { create } from "zustand";

interface FilmCrewChooserStore {
  isVisible: boolean;
  show: () => void;
  hide: () => void;
  toggle: () => void;
}

const useFilmCrewChooserStore = create<FilmCrewChooserStore>((set) => ({
  isVisible: false,
  show: () => set(() => ({ isVisible: true })),
  hide: () => set(() => ({ isVisible: false })),
  toggle: () =>
    set((state) => ({
      isVisible: !state.isVisible,
    })),
}));

export default useFilmCrewChooserStore;