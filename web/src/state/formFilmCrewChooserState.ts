import { create } from "zustand";

interface FilmCrewChooserStore {
  isVisible: boolean;
  selectedCreatorId: string | null;
  show: () => void;
  hide: () => void;
  toggle: () => void;
}

const useFilmCrewChooserStore = create<FilmCrewChooserStore>((set) => ({
  isVisible: false,
  selectedCreatorId: null,
  setSelectedCreatorId: (id: string) => {
    set({ selectedCreatorId: id });
  },
  show: () => set(() => ({ isVisible: true })),
  hide: () => set(() => ({ isVisible: false })),
  toggle: () =>
    set((state) => ({
      isVisible: !state.isVisible,
    })),
}));

export default useFilmCrewChooserStore;
