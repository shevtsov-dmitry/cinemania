import { create } from "zustand";

interface FormAddCreatorState {
  isFormAddCreatorVisible: boolean;
  showFormAddCreator: () => void;
  hideFormAddCreator: () => void;
  toggleFormAddCreator: () => void;
}

const useFormAddCreatorStore = create<FormAddCreatorState>((set) => ({
  isFormAddCreatorVisible: false,
  showFormAddCreator: () => set(() => ({ isFormAddCreatorVisible: true })),
  hideFormAddCreator: () => set(() => ({ isFormAddCreatorVisible: false })),
  toggleFormAddCreator: () =>
    set((state) => ({
      isFormAddCreatorVisible: !state.isFormAddCreatorVisible,
    })),
}));

export default useFormAddCreatorStore;