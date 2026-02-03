import { useInfiniteQuery, useQuery } from '@tanstack/react-query';
import api from '@/services/api';

// ------------------------------------
// Shared types
// ------------------------------------
export interface Product {
  id: number;
  erpId: string;
  name: string;
  price: number;
  stock: number;
  webDescription: string | null;
  isTopSelling: boolean;
  images: string[];
}

// ------------------------------------
// Homepage – top-selling products
// ------------------------------------
export function useTopProducts() {
  return useQuery<Product[]>({
    queryKey: ['products', 'top-selling'],
    queryFn: async () => {
      const { data } = await api.get<Product[]>('/api/v1/products', {
        params: { topSelling: true },
      });
      return data;
    },
  });
}

// ------------------------------------
// Catalog – infinite-scroll paginated list
// ------------------------------------
export function useCatalogProducts() {
  return useInfiniteQuery<Product[]>({
    queryKey: ['products', 'catalog'],
    queryFn: async ({ pageParam = 0 }) => {
      const { data } = await api.get<Product[]>('/api/v1/products', {
        params: { page: pageParam, size: 20 },
      });
      return data;
    },
    initialPageParam: 0,
    getNextPageParam: (lastPage, allPages) => {
      if (lastPage.length < 20) return undefined;
      return allPages.length;
    },
  });
}

// ------------------------------------
// Detail page – single product by id
// ------------------------------------
export function useProduct(id: string) {
  return useQuery<Product>({
    queryKey: ['products', id],
    queryFn: async () => {
      const { data } = await api.get<Product>(`/api/v1/products/${id}`);
      return data;
    },
    enabled: !!id,
  });
}
