import type { WeightUnit } from '@/types/domain';

const KG_TO_LB = 2.20462;

export function convertWeight(kg: number, unit: WeightUnit): number {
  if (unit === 'lb') {
    return Math.round(kg * KG_TO_LB * 10) / 10;
  }
  return kg;
}

export function convertToKg(value: number, unit: WeightUnit): number {
  if (unit === 'lb') {
    return Math.round((value / KG_TO_LB) * 10) / 10;
  }
  return value;
}

export function formatWeight(kg: number, unit: WeightUnit): string {
  const value = convertWeight(kg, unit);
  return `${value}${unit}`;
}
